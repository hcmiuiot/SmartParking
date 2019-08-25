package main.RfidProcessor;

import com.fazecast.jSerialComm.SerialPort;

import java.io.PrintWriter;
import java.util.Scanner;

public class JSerial {
    private SerialPort comPort;
    private String portDescription;
    private int baud_rate;

    /**
     * Constructor
     * @param portDescription
     * @param baud_rate
     */
    public JSerial(String portDescription, int baud_rate) {
        this.portDescription = portDescription;
        comPort = SerialPort.getCommPort(this.portDescription);
        this.baud_rate = baud_rate;
        comPort.setBaudRate(this.baud_rate);
    }
    public JSerial(){

    }

    public boolean openConnection() {
        if (this.comPort.openPort()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            return true;
        } else {
            return false;
        }
    }

    public void closeConnection() {
        comPort.closePort();
    }


    public String serialRead() {
        //will be an infinite loop if incoming data is not bound
//        this.comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        String out = "";
        Scanner in = new Scanner(this.comPort.getInputStream());
        try {
            while (in.hasNext())
                out += (in.next() + " ");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public void serialWrite(String s){
        //writes the entire string at once.
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        try{Thread.sleep(5);} catch(Exception e){}
        PrintWriter pout = new PrintWriter(comPort.getOutputStream());
        pout.print(s);
        pout.flush();
    }

    @Override
    public String toString() {
        return "JSerial | PORT: " + this.portDescription + " | BaudRate: " + this.baud_rate;
    }

    public SerialPort getComPort() {
        return comPort;
    }

    public void setComPort(SerialPort comPort) {
        this.comPort = comPort;
    }

    public String getPortDescription() {
        return portDescription;
    }

    public void setPortDescription(String portDescription) {
        this.portDescription = portDescription;
        comPort = SerialPort.getCommPort(this.portDescription);
    }

    public int getBaud_rate() {
        return baud_rate;
    }

    public void setBaud_rate(int baud_rate) {
        this.baud_rate = baud_rate;
        comPort.setBaudRate(this.baud_rate);
    }
}
