package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessResponse {

    @JsonProperty("conversationId")
    public String conversationId;

    @JsonProperty("jobId")
    public String jobId;

}
