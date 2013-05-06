package edu.drexel.TSAE;


/**
 * A data structure that holds information read in from the serial interface.
 * 
 * @author tags 
 * @version 0.1
 */
public class TSAEPacket
{
    long timeStamp;
    long startTime;
    int order;
    public TSAEPacket(String data, long start) {
        timeStamp = Long.parseLong(data.split(":")[0]);
        order = Integer.parseInt(data.split(":")[1]);
    }
    public long getTimestamp() {
        return timeStamp-startTime;
    }
    public int getOrder() {
        return order;
    }
}
