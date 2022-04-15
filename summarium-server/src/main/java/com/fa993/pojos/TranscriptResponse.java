package com.fa993.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class TranscriptResponse {

    @JsonProperty("messages")
    public Message[] messages;

    public static class Message {

        @JsonProperty("id")
        public String id;

        @JsonProperty("text")
        public String text;

        @JsonProperty("from")
        public MessageEntity from;

        @JsonProperty("startTime")
        public Date startTime;

        @JsonProperty("endTime")
        public Date endTime;

        @JsonProperty("conversationId")
        public String conversationId;

        @JsonProperty("phrases")
        public MessagePhrase[] phrases;

    }

    public static class MessageEntity {

        @JsonProperty("name")
        public String name;

        @JsonProperty("email")
        public String email;
    }

    public static class MessagePhrase {

        @JsonProperty("type")
        public String type;

        @JsonProperty("text")
        public String text;
    }

}