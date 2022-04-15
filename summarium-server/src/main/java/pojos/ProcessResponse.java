package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessResponse {

    @JsonProperty("conversationId")
    String conversationId;

    @JsonProperty("jobId")
    String jobId;

}
