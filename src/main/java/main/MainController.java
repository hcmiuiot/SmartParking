package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;

public class MainController implements Initializable {
    @FXML
    private Label lblTime;
    @FXML
    private SplitPane splitPane;
    @FXML
    private JFXComboBox<String> choosePortComboBox;

    TrackingController trackingController1;
    TrackingController trackingController2;
    JSerial mySerial;

    Parent trackingForm1, trackingForm2;

    private void RFIDSetup(){
        SerialPort[] portNames = SerialPort.getCommPorts();

        if (portNames.length > 0) {
            System.out.println("All available ports: ");
            for (int i = 0; i < portNames.length; i++){
//                choosePortComboBox.getItems().add(portNames[i].getSystemPortName());
                System.out.println(portNames[i].getSystemPortName());
            }

            mySerial = new JSerial("COM10", 9600);
            if (mySerial.openConnection()) {
                System.out.println("Open Successful");
                Runnable myRunnable =
                        () -> {
                            while (true) {
                                String s = mySerial.serialRead();
                                if (s.length() > 0){
                                    System.out.println(s );
                                    String arr[] = s.split(" ");
                                    if (arr[0].equals("R0")){
                                        trackingController1.setTextRFID(arr[1].replace(" ", ""));
                                    } else if(arr[0].equals("R1")){
                                        trackingController2.setTextRFID(arr[1].replace(" ", ""));
                                    }
                                }
                            }
                        };
                Thread thread = new Thread(myRunnable);
                thread.start();
            } else {
                System.out.println("Open Failed");
            }

        } else {
            System.out.println("No available serial port");
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        ImageProcessing.getInstance();

        DatetimeUpdater watcher = new DatetimeUpdater(lblTime);
        watcher.setDaemon(true);
        watcher.execute();

        FXMLLoader loader1 = new FXMLLoader(MainProgram.class.getResource("/" + Constants.FXML_TRACKING));
        FXMLLoader loader2 = new FXMLLoader(MainProgram.class.getResource("/" + Constants.FXML_TRACKING));

        try {
            trackingForm1 = loader1.load();
            trackingController1 = loader1.getController();
            trackingForm2 = loader2.load();
            trackingController2 = loader2.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        splitPane.setDividerPosition(0, splitPane.getWidth() / 2);
        splitPane.getItems().add(trackingForm1);
        splitPane.getItems().add(trackingForm2);

        //RFIDHandler
        this.RFIDSetup();
    }
}
