package services;

import configs.APIKey;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import pojos.*;
import utils.Utility;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static controllers.FileController.dirName;
import static controllers.FileController.done;

@Service
public class SymblAPIHandle {

    private LinkedBlockingQueue<Task> filesToProcess = new LinkedBlockingQueue<>();

    private APIKey key;

    private boolean killed = false;

    private OkHttpClient client = new OkHttpClient();

    public void addAudioFile(String filename) {
        filesToProcess.add(new Task(filename));
    }

    public void threadTask() throws InterruptedException, IOException {
        while (!killed) {
            Task t = filesToProcess.take();
            try {
                switch (t.state) {
                    case SCHEDULED:
                        //make call to symblapi to submit audio
                        break;
                    case AUDIO_PROCESSING:
                        //make call to symbl api to check job state
                        if (checkJobState(t)) {
                            t.state = CompletionState.AUDIO_PROCESSED;
                        }
                        this.filesToProcess.add(t);
                        break;
                    case AUDIO_PROCESSED:
                        TopicResponse tr = fetchTopics(t);
                        AnalyticsResponse ar = fetchAnalytics(t);
                        TranscriptResponse trr = fetchTranscript(t);
                        IndexedAudioFile fr = process(tr, trr, ar);
                        Utility.obm.writeValue(Paths.get(dirName, done, t.filename + ".json").toFile(), fr);
                        t.state = CompletionState.COMPLETED;
                        //make calls to get the corresponding data... (get messages, topics, and summary) and save json file
                        break;
                    case FAILED:
                        //notify using websocket that task failed
                        break;
                    case COMPLETED:
                        //notify using websocket that task has been successful
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                t.state = CompletionState.FAILED;
                filesToProcess.add(t);
            }
            Thread.sleep(500);
        }
    }

    public void processConversation() {
        //TODO:
    }

    public boolean checkJobState(Task t) throws IOException {

        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/job/" + t.jobId)
                .build();

        Response re = client.newCall(r).execute();

        JobStatusResponse res = Utility.obm.readValue(re.body().string(), JobStatusResponse.class);

        return res.isCompleted();
    }

    public TopicResponse fetchTopics(Task t) throws IOException {
        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/conversations/" + t.conversationId + "/topics?sentiment=true&parentRefs=true")
                .build();

        Response re = client.newCall(r).execute();

        TopicResponse res = Utility.obm.readValue(re.body().string(), TopicResponse.class);

        return res;
    }

    public TranscriptResponse fetchTranscript(Task t) throws IOException {
        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/conversations/" + t.conversationId + "/messages")
                .build();

        Response re = client.newCall(r).execute();

        TranscriptResponse res = Utility.obm.readValue(re.body().string(), TranscriptResponse.class);

        return res;
    }

    public AnalyticsResponse fetchAnalytics(Task t) throws IOException {
        Request r = new Request.Builder()
                .addHeader("Authorization", "Bearer " + key.accessToken)
                .addHeader("Content-Type", "application/json")
                .url("https://api.symbl.ai/v1/conversations/" + t.conversationId + "/analytics")
                .build();

        Response re = client.newCall(r).execute();

        AnalyticsResponse res = Utility.obm.readValue(re.body().string(), AnalyticsResponse.class);

        return res;
    }

    public IndexedAudioFile process(TopicResponse to, TranscriptResponse tr, AnalyticsResponse ar) {
        Map<String, Instant> messagesIdsToTimestamps = new HashMap<>();
        for()
    }

}

class Task {

    String filename;
    String jobId;
    String conversationId;
    CompletionState state;

    public Task(String fname) {
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


