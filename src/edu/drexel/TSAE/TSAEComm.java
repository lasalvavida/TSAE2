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
import gnu.io.NoSuchPortException;
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
    private static final String HANDSHAKE = "H"; //should be a single character
    private static final int HANDSHAKE_TIME_OUT = 10000;
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
            try {
                System.out.println("Attempting connection to: "+portId.getName());
                
                port = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);
                port.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                
                input = new BufferedReader(new InputStreamReader(port.getInputStream()));
                output = port.getOutputStream();
                
                port.addEventListener(this);
                port.notifyOnDataAvailable(true);
                
                //wait for serial port to be initialized
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
                
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
            port.close();
        }
        System.err.println("Unable to initialize TSAEComm.");
    }
    public void addSerialListener(SerialInterface listener) {
        listeners.add(listener);
    }
    public synchronized void close() {
        if (port != null) {
            System.out.println("Attempting to close port: "+port.getName()+" ...");
            try {
                output.write("C".getBytes());
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
            }
            port.removeEventListener();
            port.close();
            System.out.println("Closed port: "+port.getName()+"...");
        }
    }
    public synchronized void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                read = input.readLine();
                System.out.println("Received input: " + read);
                for (SerialInterface listener : listeners) {
                    listener.processData(read+":"+System.currentTimeMillis());
                }
                notifyAll();
            }
            catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    /**
     * Lists the available ports.
     */
    public void listPorts()
    {
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }        
    }
    /**
     * Gets a port's type.
     */
    public String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
    /**
     * Writes to the arduino and then listens for a reply. If it doesn't get anything, it returns false.
     */
    private synchronized boolean handshake() {
        try {
            System.out.println("Writing: "+HANDSHAKE);
            output.write(HANDSHAKE.getBytes());
            wait(HANDSHAKE_TIME_OUT);
            if (read != null && read.equals(HANDSHAKE)) {
                return true;
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        catch (InterruptedException e) {
            return false;
        }
        return false;
    }
}
