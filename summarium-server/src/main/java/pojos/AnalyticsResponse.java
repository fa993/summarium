package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnalyticsResponse {

    Metric[] metrics;
    AnalyticsMember[] members;

    public static class Metric {

        @JsonProperty("type")
        String type;

        @JsonProperty("percent")
        double percent;

        @JsonProperty("seconds")
        double seconds;

    }

    public static class AnalyticsMember {

        @JsonProperty("id")
        String id;

        @JsonProperty("name")
        String name;

        @JsonProperty("userId")
        String userId;

        @JsonProperty("pace")
        Pace pace;

        @JsonProperty("talkTime")
        ActivityTime talkTime;

        @JsonProperty("listenTime")
        ActivityTime listenTime;

        @JsonProperty("overlap")
        Overlap overlap;
    }

    public static class Pace {

        @JsonProperty("wpm")
        double wpm;
    }

    public static class ActivityTime {

        @JsonProperty("percentage")
        double percentage;

        @JsonProperty("seconds")
        double seconds;
    }

    public static class Overlap {

        @JsonProperty("overlapDuration")
        double overlapDuration;

        @JsonProperty("overlappingMembers")
        OverlapMember[] overlappingMembers;
    }

    public static class OverlapMember {
        @JsonProperty("id")
        String id;

        @JsonProperty("name")
        String name;

        @JsonProperty("userId")
        String userId;

        @JsonProperty("percentage")
        double percentage;

        @JsonProperty("seconds")
        double seconds;
    }

}

