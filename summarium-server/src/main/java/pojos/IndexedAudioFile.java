package pojos;

import java.time.Instant;

public class IndexedAudioFile {

    IndexedTopic[] topics;

}

class IndexedTopic {

    String text;
    Instant[] timestamps;
    double score;

}
