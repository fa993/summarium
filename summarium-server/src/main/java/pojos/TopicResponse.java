package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicResponse {

    @JsonProperty("topics")
    public Topic[] topics;

    public static class Topic {

        @JsonProperty("text")
        public String text;

        @JsonProperty("type")
        public String type;

        @JsonProperty("score")
        public double score;

        @JsonProperty("messageIds")
        public String[] messageIds;

        @JsonProperty("sentiment")
        public Sentiment sentiment;

        @JsonProperty("parentRefs")
        public ParentRef[] parentRefs;
    }

    public static class Sentiment {
        @JsonProperty("polarity")
        public Polarity polarity;
        @JsonProperty("suggested")
        public String suggested;
    }

    public static class Polarity {
        @JsonProperty("score")
        public double score;
    }

    public static class ParentRef {
        @JsonProperty("type")
        public String type;

        @JsonProperty("text")
        public String text;
    }

}