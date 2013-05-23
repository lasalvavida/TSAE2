package edu.drexel.TSAE;


/**
 * Tests the funtionality of TSAEComm
 * 
 * @author tags
 * @version 0.1
 */
public class SerialTest
{
    public static void main(String[] args) {
        TSAEComm com = new TSAEComm();
        com.init();
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
        }
        com.close();
    }
}
