package com.fa993.utils;

import com.fa993.configs.APIKey;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utility {

    public static final ObjectMapper obm = new ObjectMapper();
    public static APIKey key;
    public static String dirName = System.getProperty("java.io.tmpdir");
    public static Path dataDir;
    public static Path doneDir;
    public static Path metaDir;

    static {
        try {
            key = obm.readValue(Utility.class.getClassLoader().getResourceAsStream("api_key.json"), APIKey.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDirectories() throws IOException {
        dataDir = Files.createTempDirectory("auData");
        doneDir = Files.createTempDirectory("done");
        metaDir = Files.createTempDirectory("meta");
    }
}
