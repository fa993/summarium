package com.fa993.services;

import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import com.fa993.pojos.*;
import com.fa993.utils.Utility;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import static com.fa993.utils.Utility.*;

@Service
public class SymblAPIHandle {

    private static final MediaType audioType = MediaType.parse("audio/mp3");

    private LinkedBlockingQueue<Task> filesToProcess = new LinkedBlockingQueue<>();

    private boolean killed = false;

    private OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).connectTimeout(10, TimeUnit.MINUTES).build();

    private Set<String> doneFiles = ConcurrentHashMap.newKeySet();

    private ExecutorService exService;

    public SymblAPIHandle() {
        //create threads

        int num = 4;
        AtomicInteger cpuThreads = new AtomicInteger(num);

        exService = Executors.newFixedThreadPool(cpuThreads.intValue(), r -> {
            Thread t = new Thread(r, "Summarium-" + cpuThreads.getAndDecrement());
            t.setDaemon(true);
            return t;
        });

        for(int i = 0; i < num; i++) {
            exService.submit(this::threadTask);
        }


        System.out.println("Instantiated");
    }

    public void addAudioFile(String uuid, String filename) {
        filesToProcess.add(new Task(uuid, filename));
    }

    public void threadTask() {
        while (!killed) {
            Task t = null;
            try {
                t = filesToProcess.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            try {
                switch (t.state) {
                    case SCHEDULED:
                        //make call to symblapi to submit audio

                        ProcessResponse resp = processConversation(t);

                        t.conversationId = resp.conversationId;
                        t.jobId = resp.jobId;
                        t.state = CompletionState.AUDIO_PROCESSING;
                        this.filesToProcess.add(t);
                        break;
                    case AUDIO_PROCESSING:
                        //make call to symbl api to check job state
                        System.out.println("Task Checking Status, " + t.state);
                        t.exponentialBackOffStep = t.exponentialBackOffStep > 7 ? 8 : t.exponentialBackOffStep + 1;
                        if (Instant.now().isAfter(t.lastStatusCheck.plus((long) Math.pow(2, t.exponentialBackOffStep) + 4, ChronoUnit.SECONDS)) && checkJobState(t)) {
                            t.state = CompletionState.AUDIO_PROCESSED;
                            System.out.println("Completed Processing");
                            t.exponentialBackOffStep = 0;
                        }
                        this.filesToProcess.add(t);
                        break;
                    case AUDIO_PROCESSED:
                        //make calls to get the corresponding data... (get messages, topics, and summary) and save json file
                        TopicResponse tr = fetchTopics(t);
                        AnalyticsResponse ar = fetchAnalytics(t);
                        TranscriptResponse trr = fetchTranscript(t);
                        IndexedAudioFile fr = process(tr, trr, ar);
                        Utility.obm.writeValue(new File(doneDir.toFile(), t.uuid + ".json"), fr);
                        t.state = CompletionState.COMPLETED;
                        this.filesToProcess.add(t);
                        System.out.println("Completed Analytics");
                        break;
                    case FAILED:
                        //notify using websocket that task failed
                        break;
                    case COMPLETED:
                        System.out.println("Done " + t.uuid);
                        this.doneFiles.add(t.uuid);
                        //notify using websocket that task has been successful
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                t.state = CompletionState.FAILED;
                filesToProcess.add(t);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @PreDestroy
    public void kill() {
        this.killed = true;
        exService.shutdown();
        System.out.println("Killed");
    }

     public boolean isDone(String id) {
        return doneFiles.contains(id);
     }

    public ProcessResponse processConversation(Task t) throws IOException {
        //TODO:

        URL url = new URL("https://api.symbl.ai/v1/process/audio?name=" + t.uuid + "&confidenceThreshold=0.6&sentiment=true");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "audio/mp3");
        conn.setRequestProperty("Authorization", "Bearer " + Utility.key.accessToken);
        IOUtils.copy(new FileInputStream(t.filename), conn.getOutputStream());
        String re = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);

        System.out.println(re);

//        File fr = new File(t.filename);
//
//        FormBody bdy = new FormBody.Builder().
//
//        Request r = new Request.Builder()
//                .addHeader("Authorization", "Bearer " + Utility.key.accessToken)
//                .addHeader("Content-Type", "audio/mp3")
//                .url("https://api.symbl.ai/v1/process/audio?name=" + t.filename + "&confidenceThreshold=0.6&sentiment=true")
//                .post(new .Builder().setType(MultipartBody.FORM).addFormDataPart("file", fr.getName(), RequestBody.Companion.create(fr, audioType)).build())
//                .build();
//
//        Response re = client.newCall(r).execute();

//        ProcessResponse res = Utility.obm.readValue(re.body().string(), ProcessResponse.class);
        ProcessResponse res = Utility.obm.readValue(re, ProcessResponse.class);
        obm.writeValue(new File(metaDir.toFile(), t.uuid + ".json"), res);
        return res;
    }

    public boolean checkJobState(Task t) throws IOException {

        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + Utility.key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/job/" + t.jobId)
                .build();

        Response re = client.newCall(r).execute();

        JobStatusResponse res = Utility.obm.readValue(re.body().string(), JobStatusResponse.class);
        t.lastStatusCheck = Instant.now();
        return res.isCompleted();
    }

    public TopicResponse fetchTopics(Task t) throws IOException {
        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + Utility.key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/conversations/" + t.conversationId + "/topics?sentiment=true&parentRefs=true")
                .build();

        Response re = client.newCall(r).execute();

        TopicResponse res = Utility.obm.readValue(re.body().string(), TopicResponse.class);

        System.out.println("Completed Topics");

        return res;
    }

    public TranscriptResponse fetchTranscript(Task t) throws IOException {
        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + Utility.key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/conversations/" + t.conversationId + "/messages")
                .build();

        Response re = client.newCall(r).execute();

        TranscriptResponse res = Utility.obm.readValue(re.body().string(), TranscriptResponse.class);

        System.out.println("Completed Transcript");

        return res;
    }

    public AnalyticsResponse fetchAnalytics(Task t) throws IOException {
        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + Utility.key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/conversations/" + t.conversationId + "/analytics")
                .build();

        Response re = client.newCall(r).execute();

        AnalyticsResponse res = Utility.obm.readValue(re.body().string(), AnalyticsResponse.class);

        System.out.println("Completed Analytics");

        return res;
    }

    public IndexedAudioFile process(TopicResponse to, TranscriptResponse tr, AnalyticsResponse ar) {
        Map<String, Date> messagesIdsToTimestamps = new HashMap<>();

        IndexedAudioFile audi = new IndexedAudioFile();
        List<IndexedAudioFile.IndexedTopic> tps = new ArrayList<>();

        for (TranscriptResponse.Message ms : tr.messages) {
            messagesIdsToTimestamps.put(ms.id, ms.startTime);
        }

        Arrays.sort(to.topics, (o1, o2) -> (int) ((o2.score - o1.score) * 1000));

        for (int i = 0; i < to.topics.length && to.topics[i].score > 0.4; i++) {
            IndexedAudioFile.IndexedTopic tp = new IndexedAudioFile.IndexedTopic();
            tp.text = to.topics[i].text;
            tp.timestamps = new Date[to.topics[i].messageIds.length];
            for (int j = 0; j < to.topics[i].messageIds.length; j++) {
                String msgId = to.topics[i].messageIds[j];
                tp.timestamps[j] = messagesIdsToTimestamps.get(msgId);
                tp.score = to.topics[i].score;
            }
            tps.add(tp);
        }
        audi.topics = tps.toArray(new IndexedAudioFile.IndexedTopic[0]);
        return audi;
    }

    public void pushCustomTask(String id, int taskStatus) throws IOException {
        CompletionState st = null;
        switch (taskStatus) {
            case 0:
                st = CompletionState.COMPLETED;
                break;
            case 1:
                st = CompletionState.FAILED;
                break;
            case 2:
                st = CompletionState.SCHEDULED;
                break;
            case 3:
                st = CompletionState.AUDIO_PROCESSING;
                break;
            case 4:
                st = CompletionState.AUDIO_PROCESSED;
                break;
            default:
                return;
        }
        ProcessResponse res = obm.readValue(new File(metaDir.toFile(), id + ".json"), ProcessResponse.class);
        Task t = new Task(id, new File(dataDir.toFile(), id + ".mp3").getAbsolutePath());
        t.conversationId = res.conversationId;
        t.jobId = res.jobId;
        t.state = st;
        this.filesToProcess.add(t);
    }

}

class Task {

    String uuid;
    String filename;
    String jobId;
    String conversationId;
    CompletionState state;
    int exponentialBackOffStep = 0;
    Instant lastStatusCheck = Instant.MIN;

    public Task(String uuid, String fname) {
        this.uuid = uuid;
        this.filename = fname;
        this.state = CompletionState.SCHEDULED;
    }
}

enum CompletionState {

    COMPLETED,
    FAILED,
    SCHEDULED,
    AUDIO_PROCESSING,
    AUDIO_PROCESSED

}


