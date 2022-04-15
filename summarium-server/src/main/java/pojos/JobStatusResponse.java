package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobStatusResponse {

    @JsonProperty("id")
    String id;

    @JsonProperty("status")
    String status;

    public boolean isCompleted() {
        return status == "completed";
    }

}
