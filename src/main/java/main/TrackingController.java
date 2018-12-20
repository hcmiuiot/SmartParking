package main;

import Camera.CameraStreamer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TrackingController implements Initializable {

    @FXML
    private ImageView imgFont;
    @FXML
    private ImageView imgBehind;
    @FXML
    private ImageView imgPlate;
    @FXML
    private ImageView imgCamFont;
    @FXML
    private ImageView imgCamBehind;
    @FXML
    private JFXTextField txtPlateNumber;
    @FXML
    private JFXTextField txtRFID;
    @FXML
    private JFXButton enterOutBtn;

    @FXML
    private Label lbl_checkInTime;

    @FXML
    private Label lbl_checkOutTime;

    @FXML
    private Label lbl_parkingDuration;

    @FXML
    private Label lbl_parkingFee;

    @FXML
    private JFXButton btn_cancel;

    private Image defaultImg = new Image("/" + Constants.DEFAULT_IMG_DIR);
    private Date timeIn;
    private Date timeOut;
    private Vehicle currentVehicle;
    /**
     * 0 - Waiting
     * 1 - Entering
     * 2 - Outing
     */
    private byte state = 0;

    private ScheduledExecutorService timer;
    private int deviceIndex = 0;
    private VideoCapture capture;
    private Mat frame;
    private Mat m1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeToWaitingMode();

        txtRFID.textProperty().addListener((observable, oldValue, newValue) -> {
            // Real purpose
            if (RFIDHandler.checkValidRFID(newValue.toUpperCase())) {
                Platform.runLater(() -> {
                    System.out.println("Valid RFID");
                    enterOutBtn.setDisable(false);
                    enterOutBtn.fire();
                });
                // NullPointerException...
//                enterOutBtn.fire();
            } else {
                Platform.runLater(() -> {
                    enterOutBtn.setDisable(true);
                });
            }
//            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });
    }


    public void changeToWaitingMode() {
        state = 0;
        enterOutBtn_changeToYellow();
        txtPlateNumber.setDisable(true);
        txtRFID.setDisable(false);
        btn_cancel.setDisable(true);

        txtPlateNumber.setText("");
        txtRFID.setText("");

        lbl_checkInTime.setText("-");
        lbl_checkOutTime.setText("-");
        lbl_parkingDuration.setText("-");
        lbl_parkingFee.setText("- VND");
    }

    public void changeToConfirm_EnterMode() {
        state = 1;
        enterOutBtn_changeToGreen();
        txtPlateNumber.setDisable(false);
        txtRFID.setDisable(true);
        btn_cancel.setDisable(false);

        currentVehicle.setFrontImg(imgCamFont.getImage());
        currentVehicle.setPlateImg(imgCamBehind.getImage());
        currentVehicle.setTimeIn(new Date());

        imgFont.setImage(currentVehicle.getFrontImg());
        imgBehind.setImage(currentVehicle.getPlateImg());
        timeIn = currentVehicle.getTimeIn();

        lbl_checkInTime.setText(MainProgram.getSimpleDateFormat().format(timeIn));
        lbl_parkingDuration.setText("0");
        lbl_parkingFee.setText("0 VND");
    }

    public void changeToConfirm_OutMode(Vehicle inputVehicle) {
        state = 2;
        enterOutBtn_changeToRed();
        txtPlateNumber.setDisable(true);
        txtRFID.setDisable(true);
        btn_cancel.setDisable(false);

        imgFont.setImage(inputVehicle.getFrontImg());
        imgBehind.setImage(inputVehicle.getPlateImg());
        timeIn = currentVehicle.getTimeIn();
        timeOut = new Date();
        long duration = getDateDiff(inputVehicle.getTimeIn(), timeOut, TimeUnit.HOURS);


        txtPlateNumber.setText(inputVehicle.getPlateNumber());
        lbl_checkInTime.setText(MainProgram.getSimpleDateFormat().format(timeIn));
        lbl_checkOutTime.setText(MainProgram.getSimpleDateFormat().format(timeOut));
        lbl_parkingDuration.setText(duration + " Hours");
        lbl_parkingFee.setText(VehicleManage.getInstance().calculateParkingFee(duration) + " VND");
    }

    @FXML
    void enterOutBtn_onAction(ActionEvent event) {
        if (state == 0) {
            System.out.println(txtRFID.getText());
            enterOutBtn.setText("...");
            currentVehicle = VehicleManage.getInstance().getXeByRfidFromParkingList(txtRFID.getText());
            if (currentVehicle != null) {
                Platform.runLater(() -> {
                    changeToConfirm_OutMode(currentVehicle);
                });
            } else {
                Platform.runLater(() -> {
                    changeToConfirm_EnterMode();
                });
            }
        } else if (state == 1) {
//            currentVehicle = new Vehicle(txtRFID.getText(), null, null, txtPlateNumber.getText(), new Date());
            VehicleManage.addXe(currentVehicle);
            System.out.println(currentVehicle + " IN");
            currentVehicle = null;
            changeToWaitingMode();
            resetImg();
        } else if (state == 2) {
            currentVehicle.changeStutusToLeft();
            VehicleManage.getInstance().moveXeToOtherList(currentVehicle);
            System.out.println(currentVehicle + " OUT");
            currentVehicle = null;
            changeToWaitingMode();
            resetImg();
        }
    }

    @FXML
    void onTest(ActionEvent event) {
        //streamVideo();
//		ImageProcessing.train("D:\\IT\\IdeaProjects\\SmartParking\\data\\demo\\char", "D:\\IT\\IdeaProjects\\SmartParking\\trainedData.txt");
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./data/img"));
        File img = chooser.showOpenDialog(null);
        if (img != null) {
            DataPacket packet = new DataPacket(img);
            ImageProcessing.setImage(imgFont, packet.getOriginMat());
            ImageProcessing.setImage(imgBehind, packet.getOriginMat());
            ImageProcessing.setImage(imgPlate, packet.getDetectedPlate());
            txtPlateNumber.setText(packet.getLicenseNumber());
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        System.out.println("Reach here");
        Platform.exit();
    }

    public CameraStreamer cameraStreamer;
    public CameraStreamer cameraStreamer2;

    @FXML
    void onTestAll(ActionEvent event) {
//		demoProcessAllImgs();
//		ImageProcessing.train();

//		Platform.runLater(() -> {
        cameraStreamer = new CameraStreamer(0, imgCamFont);
        cameraStreamer2 = new CameraStreamer(0, imgCamBehind);
//
//            cameraStreamer.setFps(50);
//            cameraStreamer2.setFps(50);

        cameraStreamer.startStream();
        cameraStreamer2.startStream();
//		});

        //Thread thread = new Thread(new CameraStreamer(0, imgCamFont));
//		streamVideo();
    }


    private void demoProcessAllImgs() {
        File folder = new File("./img");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());

//		        ImageProcessing.getLicensePlateNumber(listOfFiles[i]);

            }

//		      } else if (listOfFiles[i].isDirectory()) {
//		        System.out.println("Directory " + listOfFiles[i].getName());
//		      }

        }
    }


    @FXML
    private void onConfig(ActionEvent event) {
//		cameraStreamer.stopStream();
//		cameraStreamer2.stopStream();
        FXMLLoader configLoader = new FXMLLoader(getClass().getResource("/" + Constants.FXML_TRACKING_CONFIG));
        try {
            AnchorPane configDialog = configLoader.load();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(configDialog));
            TrackingConfigController configController = configLoader.getController();
            configController.setTrackingController(this);
//			stage.show();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int focusWidth, focusHeight, focusX, focusY;

    public void setFocusConfig(int width, int height, int x, int y) {
        focusWidth = width;
        focusHeight = height;
        focusX = x;
        focusY = y;
    }

    @FXML
    void onEnterRFID(ActionEvent event) {
        System.out.println("Enter RFID: " + txtRFID.getText());
        txtRFID.clear();
    }

    @FXML
    void cancelSession(ActionEvent event) {
        changeToWaitingMode();
    }

    public void setTextRFID(String textRFID) {
        this.txtRFID.setText(textRFID);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }


    private void enterOutBtn_changeToGreen() {
        this.enterOutBtn.setText("Enter");
        this.enterOutBtn.setStyle("-fx-background-color:  #4CAF50;");
    }

    private void enterOutBtn_changeToRed() {
        this.enterOutBtn.setText("Out");
        this.enterOutBtn.setStyle("-fx-background-color:  #D32F2F;");
    }

    private void enterOutBtn_changeToYellow() {
        this.enterOutBtn.setText("Check");
        this.enterOutBtn.setStyle("-fx-background-color:  #F57C00;");
    }

    private void resetImg() {
        imgBehind.setImage(defaultImg);
        imgFont.setImage(defaultImg);
    }

    public byte getState() {
        return state;
    }

    //	@Override
//	public void finalizeStream() throws IOException {
//		shutdown();
//	}
}


