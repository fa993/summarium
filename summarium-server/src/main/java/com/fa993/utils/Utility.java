package com.fa993.utils;

import com.fa993.configs.APIKey;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utility {

    public static final ObjectMapper obm = new ObjectMapper();
    private static final int TEMP_DIR_ATTEMPTS = 10000;
    public static APIKey key;
    public static String dirName = System.getProperty("java.io.tmpdir");
    public static File dataDir;
    public static File doneDir;
    public static File metaDir;

    static {
        try {
            key = obm.readValue(Utility.class.getClassLoader().getResourceAsStream("api_key.json"), APIKey.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDirectories() throws IOException {
        dataDir = createTempDir("SummariumAuData");
        doneDir = createTempDir("SummariumDone");
        metaDir = createTempDir("SummariumMeta");
    }

    public static File createTempDir(String base) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = base;

        File tempDir = new File(baseDir, baseName);
        tempDir.mkdir();

        return tempDir;
    }

}
