package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pojos.IndexedAudioFile;
import services.SymblAPIHandle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static utils.Utility.obm;

@Controller
public class FileController {

    public static String dirName = System.getProperty("java.io.tmpdir");
    public static String audata = "toProcess";
    public static String done = "done";

    @Autowired
    SymblAPIHandle handle;

    @PostMapping("/processaudio")
    public ResponseEntity<?> handleFileUpload(@RequestParam("audiofile")MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        try {
            File f = Paths.get(dirName, audata, uuid + ".mp3").toFile();
            file.transferTo(f);
            handle.addAudioFile(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.accepted().body(uuid);
    }

    @GetMapping("/gettopics")
    public ResponseEntity<IndexedAudioFile> getProcessedData(@RequestParam("id") String identify) {
        try {
            return ResponseEntity.ok(obm.readValue(Paths.get(dirName, done, identify + ".json").toFile(), IndexedAudioFile.class));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }




}
