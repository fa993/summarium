package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Date;

public class TranscriptResponse {

    @JsonProperty("messages")
    Message[] messages;

    public static class Message {

        @JsonProperty("id")
        String id;

        @JsonProperty("text")
        String text;

        @JsonProperty("from")
        MessageEntity from;

        @JsonProperty("startTime")
        Date startTime;

        @JsonProperty("endTime")
        Date endTime;

        @JsonProperty("conversationId")
        String conversationId;

        @JsonProperty("phrases")
        MessagePhrase[] phrases;

    }

    public static class MessageEntity {

        @JsonProperty("name")
        String name;

        @JsonProperty("email")
        String email;
    }

    public static class MessagePhrase {

        @JsonProperty("type")
        String type;

        @JsonProperty("text")
        String text;
    }

}