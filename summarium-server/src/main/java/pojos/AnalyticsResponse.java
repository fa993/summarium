package pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnalyticsResponse {

    public Metric[] metrics;
    public AnalyticsMember[] members;

    public static class Metric {

        @JsonProperty("type")
        public String type;

        @JsonProperty("percent")
        public double percent;

        @JsonProperty("seconds")
        public double seconds;

    }

    public static class AnalyticsMember {

        @JsonProperty("id")
        public String id;

        @JsonProperty("name")
        public String name;

        @JsonProperty("userId")
        public String userId;

        @JsonProperty("pace")
        public Pace pace;

        @JsonProperty("talkTime")
        public ActivityTime talkTime;

        @JsonProperty("listenTime")
        public ActivityTime listenTime;

        @JsonProperty("overlap")
        public Overlap overlap;
    }

    public static class Pace {

        @JsonProperty("wpm")
        public double wpm;
    }

    public static class ActivityTime {

        @JsonProperty("percentage")
        public double percentage;

        @JsonProperty("seconds")
        public double seconds;
    }

    public static class Overlap {

        @JsonProperty("overlapDuration")
        public double overlapDuration;

        @JsonProperty("overlappingMembers")
        public OverlapMember[] overlappingMembers;
    }

    public static class OverlapMember {
        @JsonProperty("id")
        public String id;

        @JsonProperty("name")
        public String name;

        @JsonProperty("userId")
        public String userId;

        @JsonProperty("percentage")
        public double percentage;

        @JsonProperty("seconds")
        public double seconds;
    }

}

