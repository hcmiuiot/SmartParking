package main.JavaFxGui.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.fazecast.jSerialComm.SerialPort;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import main.Constants;
import main.JavaFxGui.DatetimeUpdater;
import main.ImageProcessor.PlateNumberProcessing.ImageProcessing;
import main.MainProgram;
import main.RfidProcessor.JSerial;

public class MainController implements Initializable {
    //region Properties

    //region JavaFx
    @FXML
    private Label lblTime;
    @FXML
    private SplitPane splitPane;
    @FXML
    private JFXComboBox<Label> choosePortComboBox;
    @FXML
    private JFXButton btn_rfidConnect;
    @FXML
    private JFXButton btn_rfidStop;
    @FXML
    private JFXButton btn_rfidRefresh;
    //endregion

    //region Controller
    TrackingController trackingController1;
    TrackingController trackingController2;

    Parent trackingForm1, trackingForm2;
    //endregion

    //region RfidClient
    private ArrayList<SerialPort> portNames = new ArrayList<>();
    private JSerial mySerial = new JSerial();
    private volatile Boolean portStatus;
    private volatile Boolean cancelled = false;
    //endregion

    //endregion

    //region Methods

    //region Initialization
    public void initialize(URL location, ResourceBundle resources) {
        choosePortComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    btn_rfidConnect.setDisable(false);
                }
        );

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

        trackingController1.setName("Left Tracking");
        trackingController2.setName("Right Tracking");

        //RFIDHandler
        this.refreshPortList();
    }
    //endregion

    //region JavaFx
    @FXML
    void onRFIDConnectAction(ActionEvent event) {

        if (this.connectPort()) {
            Platform.runLater(() -> {
                btn_rfidConnect.setDisable(true);
                btn_rfidStop.setDisable(false);
            });
            this.start();
        }
    }

    @FXML
    void onRFIDRefreshAction(ActionEvent event) {
        this.refreshPortList();
    }

    @FXML
    void onRFIDStopAction(ActionEvent event) {
        this.closePort();
        this.refreshPortList();
        Platform.runLater(() -> {
            btn_rfidConnect.setDisable(false);
            btn_rfidStop.setDisable(true);
        });
    }
    //endregion


    //region RfidClient
    private void refreshPortList() {
        choosePortComboBox.getItems().clear();
        portNames = new ArrayList<>(Arrays.asList(SerialPort.getCommPorts()));
        if (portNames.size() > 0) {
            System.out.println("All available ports: ");
            choosePortComboBox.setPromptText("---Choose Port---");
            for (int i = 0; i < portNames.size(); i++) {
                choosePortComboBox.getItems().add(new Label(portNames.get(i).getSystemPortName()));
                System.out.println(portNames.get(i).getSystemPortName());
            }
        } else {
            System.out.println("No available serial port");
            choosePortComboBox.setPromptText("---No Port Available---");
        }
    }

    private Boolean connectPort() {
        mySerial = new JSerial(choosePortComboBox.getValue().getText(), 9600);
        if (mySerial.openConnection()) {
            this.portStatus = true;
        } else {
            this.portStatus = false;
        }
        return portStatus;
    }

    public void closePort() {
        mySerial.closeConnection();
        portStatus = false;
    }

    private void start() {
        Runnable RFIDrunner =
                () -> {
                    System.out.println(cancelled);
                    System.out.println(portStatus);
                    while (!cancelled && portStatus) {
                        String s = "";
                        try {
                            s = mySerial.serialRead();
                        } catch (Exception e) {
                            btn_rfidStop.fire();
                            System.err.println("RFID Disconnected");
                        }
                        if (s.length() > 0) {
//                            System.out.println(s);
                            String arr[] = s.split(" ");
                            if (arr[0].equals("R0")) {
                                if (trackingController1.getState() == 0) {
                                    Platform.runLater(() -> {
                                        trackingController1.setTextRFID(arr[1].replace(" ", ""));
                                    });
                                }
                            } else if (arr[0].equals("R1")) {
                                if (trackingController2.getState() == 0) {
                                    Platform.runLater(() -> {
                                        trackingController2.setTextRFID(arr[1].replace(" ", ""));
                                    });
                                }
                            }
                        }
                    }
                };
        Thread RFIDThread = new Thread(RFIDrunner);
        RFIDThread.start();
    }
    //endregion
    //endregion
}
