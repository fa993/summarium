package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicResponse {

    @JsonProperty("topics")
    Topic[] topics;

}

class Topic {

    @JsonProperty("text")
    String text;

    @JsonProperty("type")
    String type;

    @JsonProperty("score")
    double score;

    @JsonProperty("messageIds")
    String[] messageIds;

    @JsonProperty("sentiment")
    Sentiment sentiment;

    @JsonProperty("parentRefs")
    ParentRef[] parentRefs;
}

class Sentiment {
    @JsonProperty("polarity")
    Polarity polarity;
    @JsonProperty("suggested")
    String suggested;
}

class Polarity {
    @JsonProperty("score")
    double score;
}

class ParentRef {
    @JsonProperty("type")
    String type;
    @JsonProperty("text")
    String text;
}