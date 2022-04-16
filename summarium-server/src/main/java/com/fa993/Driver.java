package com.fa993;

import com.fa993.utils.Utility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Driver {

    public static void main(String[] args) throws IOException {
        Utility.createDirectories();
        SpringApplication.run(Driver.class, args);
    }

}
