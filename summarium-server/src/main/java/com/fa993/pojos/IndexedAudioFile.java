package com.fa993.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexedAudioFile {

    public int silenceTime;
    public IndexedTopic[] topics;

    public static class IndexedTopic {

        public String text;
        public long[] timestamps;
        public double score;

    }


}

