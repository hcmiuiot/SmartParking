package main;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;

public class RFIDHandler {
    private Boolean portStatus;
    private volatile Boolean cancelled;
    private TrackingController trackingController_L;
    private TrackingController trackingController_R;
    private JFXComboBox<Label> choosePortComboBox;
    private SerialPort[] portNames;
    private JSerial mySerial = new JSerial();


    private Runnable RFIDrunner =
            () -> {
                while (!this.cancelled && this.portStatus) {
                    String s = mySerial.serialRead();
                    if (s.length() > 0) {
                        System.out.println(s);
                        String arr[] = s.split(" ");
                        if (arr[0].equals("R0")) {
                            trackingController_L.setTextRFID(arr[1].replace(" ", ""));
                        } else if (arr[0].equals("R1")) {
                            trackingController_R.setTextRFID(arr[1].replace(" ", ""));
                        }
                    }
                }
            };
    private Thread RFIDThread = new Thread(RFIDrunner);

    public RFIDHandler(TrackingController trackingController_L, TrackingController trackingController_R, JFXComboBox<Label> choosePortComboBox) {
        this.trackingController_L = trackingController_L;
        this.trackingController_R = trackingController_R;
        this.choosePortComboBox = choosePortComboBox;
    }

    public void refreshPortList() {
        portNames = SerialPort.getCommPorts();
        if (portNames.length > 0) {
            System.out.println("All available ports: ");
            for (int i = 0; i < portNames.length; i++) {
                choosePortComboBox.getItems().add(new Label(portNames[i].getSystemPortName()));
                System.out.println(portNames[i].getSystemPortName());
            }
        } else {
            System.out.println("No available serial port");
            choosePortComboBox.setPromptText("---No Port Available---");
        }
    }

    public void connectPort() {
        mySerial = new JSerial(choosePortComboBox.getValue().getText(), 9600);
        if (mySerial.openConnection()) {
            this.portStatus = true;
        } else {
            this.portStatus = false;
        }
    }

    public void closePort(){
        mySerial.closeConnection();
        portStatus = false;
    }

    public void start() {
        RFIDThread.start();
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
