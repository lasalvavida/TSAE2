package edu.drexel.TSAE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.ArrayList;


/**
 * A service that locates the Arduino board, performs a handshake and then reads data
 * to the designated callbacks.
 * 
 * @author tags
 * @version 0.1
 */
public class TSAEComm implements SerialPortEventListener {
    private SerialPort port;
    private BufferedReader input;
    private OutputStream output;
    private String read;
    private ArrayList<SerialInterface> listeners;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;
    private static final String HANDSHAKE = "TEST";
    private static final int HANDSHAKE_TIME_OUT = 5000;
    public TSAEComm() {
        listeners = new ArrayList<SerialInterface>();
    }
    /**
     * Initializes a serial communication connection with the arduino.
     */
    public void init() {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while(ports.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) ports.nextElement();
            System.out.println("Attempting connection to: "+portId.getName());
            try {
                port = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);
                port.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                
                input = new BufferedReader(new InputStreamReader(port.getInputStream()));
                output = port.getOutputStream();
                
                port.addEventListener(this);
                port.notifyOnDataAvailable(true);
                
                if(handshake()) {
                    System.out.println("Successfully connected to: "+portId.getName());
                    return;
                }
                else {
                    System.out.println("Failed connection with: "+portId.getName());
                }
            }
            catch (IOException e) {
                System.err.println("Error while reading/writing to: "+portId.getName()+": "+e.getMessage());
            }
            catch (PortInUseException e) {
                System.err.println("Port in use: "+portId.getName()+": "+e.getMessage());
            }
            catch (UnsupportedCommOperationException e) {
                System.err.println("Unsupported comm operation on port: "+portId.getName()+": "+e.getMessage());
            }
            catch (TooManyListenersException e) {
                System.err.println("Too many listeners on port: "+portId.getName()+": "+e.getMessage());
            }
            close();
        }
        System.err.println("Unable to initialize TSAEComm.");
    }
    public void addSerialListener(SerialInterface listener) {
        listeners.add(listener);
    }
    public synchronized void close() {
        if (port != null) {
            port.removeEventListener();
            port.close();
        }
    }
    public synchronized void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                read = input.readLine();
                for (SerialInterface listener : listeners) {
                    listener.processData(read+":"+System.currentTimeMillis());
                }
                notifyAll();
            }
            catch (IOException e) {
            }
        }
    }
    /**
     * Writes to the arduino and then listens for a reply. If it doesn't get anything, it returns false.
     */
    private boolean handshake() {
        try {
            output.write(HANDSHAKE.getBytes());
            wait(HANDSHAKE_TIME_OUT);
            if (read != null && read.equals(HANDSHAKE)) {
                return true;
            }
        }
        catch (IOException e) {
        }
        catch (InterruptedException e) {
            return false;
        }
        return false;
    }
}
