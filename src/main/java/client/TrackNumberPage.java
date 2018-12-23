package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TrackNumberPage {

    private static Logger logger = LoggerFactory.getLogger(TrackNumberPage.class);

    private int currentIdx;
    public int trackNumSize;
    private static final int PAGE_SIZE = 999;

    public List<String> trackNumbers;

    public TrackNumberPage(List<String> trackNumbers) {
        this.trackNumbers = trackNumbers;
        currentIdx = 0;
        trackNumSize = trackNumbers.size();
    }

    public int getTrackNumSize(){
        return trackNumSize;
    }

    public List<String> getTrackNumbers(){
        return trackNumbers;
    }

    public String getElement(){
        if (trackNumSize <= PAGE_SIZE) {
            currentIdx = trackNumSize;
            return Utils.joinElement(trackNumbers);
        }
        int endIdx = currentIdx + PAGE_SIZE;
        if (endIdx > trackNumSize)
            endIdx = trackNumSize;
        List<String> subList = trackNumbers.subList(currentIdx, endIdx);
        logger.info("start index : " + currentIdx + " total size: "+ trackNumSize);

        currentIdx = endIdx;
        return Utils.joinElement(subList);
    }

    public boolean hasElement(){
        return trackNumSize > currentIdx;
    }
}
