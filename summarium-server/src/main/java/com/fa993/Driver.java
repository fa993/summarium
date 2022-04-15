package com.fa993;

import com.fa993.configs.APIKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fa993.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class Driver {

    public static void main(String[] args) throws IOException {
        Utility.createDirectories();
        SpringApplication.run(Driver.class, args);
    }

}
