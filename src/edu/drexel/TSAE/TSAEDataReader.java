package edu.drexel.TSAE;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses TSAEComm to gather data from the arduino and populate an array for later interpretation.
 * 
 * @author tags
 * @version 0.1
 */
public class TSAEDataReader implements SerialInterface {
    ArrayList<TSAEPacket> packets;
    AtomicBoolean reading;
    long startTime;
    public TSAEDataReader() {
        reading = new AtomicBoolean(false);
        packets = new ArrayList<TSAEPacket>();
    }
    public void processData(String data) {
         if(reading.get()) {
             packets.add(new TSAEPacket(data, startTime));
         }
    }
    public void readData(long time) {
        try {
            reading.set(true);
            startTime = System.currentTimeMillis();
            Thread.sleep(time);
            reading.set(false);
        }
        catch (InterruptedException e) {
            System.err.println("Error while sleeping: "+e.getMessage());
        }
    }
}