package com.fa993.pojos;

import java.util.Date;

public class IndexedAudioFile {

    public IndexedTopic[] topics;

    public static class IndexedTopic {

        public String text;
        public Date[] timestamps;
        public double score;

    }


}

