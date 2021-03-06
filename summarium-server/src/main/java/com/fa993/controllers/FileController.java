package com.fa993.controllers;

import com.fa993.pojos.IndexedAudioFile;
import com.fa993.services.SymblAPIHandle;
import com.fa993.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.fa993.utils.Utility.obm;

@Controller
@CrossOrigin()
public class FileController {

    @Autowired
    SymblAPIHandle handle;

    @PostMapping("/processaudio")
    public ResponseEntity<?> handleFileUpload(@RequestParam("upload")MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        try {
            File f = new File(Utility.dataDir, uuid + ".mp3");
            file.transferTo(f);
            handle.addAudioFile(uuid, f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.accepted().body(uuid);
    }

    @GetMapping("/checkstatus")
    public ResponseEntity<?> getStatus(@RequestParam("id") String id) {
        if(handle.isError(id)) {
            return ResponseEntity.ok("error");
        }
        return ResponseEntity.ok(handle.isDone(id));
    }

    @GetMapping("/gettopics")
    public ResponseEntity<IndexedAudioFile> getProcessedData(@RequestParam("id") String identify) {
        try {
            return ResponseEntity.ok(obm.readValue(new File(Utility.doneDir, identify + ".json"), IndexedAudioFile.class));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/debugtask")
    public ResponseEntity<?> pushCustomTask(@RequestParam("id") String id, @RequestParam("taskState") int taskState) {
        try {
            handle.pushCustomTaskV2(id, taskState);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}
