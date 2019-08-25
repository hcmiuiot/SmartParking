package main.JavaFxGui.Controller;

import Camera.CameraStreamer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import main.Constants;
import main.Domain.ParkingSession;
import main.Domain.SessionParkingServices;
import main.ImageProcessor.EmotionalProcessing.EmotionDetector;
import main.ImageProcessor.EmotionalProcessing.EnumEmotion;
import main.ImageProcessor.PlateNumberProcessing.DataPacket;
import main.ImageProcessor.PlateNumberProcessing.ImageProcessing;
import main.MainProgram;
import main.RfidProcessor.RFIDHandler;
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

    //region Properties

    //region JavaFX
    @FXML
    private ImageView imgFont;
    @FXML
    private ImageView imgBack;
    @FXML
    private ImageView imgPlate;
    @FXML
    private ImageView imgCamFont;
    @FXML
    private ImageView imgCamBack;
    @FXML
    private ImageView imgCamPlate;
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
    private Label lbl_emotionVal;
    @FXML
    private Label lbl_parkingFee;
    @FXML
    private JFXButton btn_cancel;
    //endregion

    //region Application State
    private Image defaultImg = new Image("/" + Constants.DEFAULT_IMG_DIR);
    private Image defaultImg_plate = new Image("/" + Constants.DEFAULT_IMG_PLATE_DIR);

    private Date timeIn;
    private Date timeOut;
    private EnumEmotion detecedEmotion = EnumEmotion.UNKNOWN;

    private ParkingSession currentParkingSession;

    private String name;
    /**
     * 0 - Both Enter and Out lane (Default)
     * 1 - Only Enter lane
     * 2 - Only Out lane
     */
    private byte role = 0;
    /**
     * 0 - Waiting
     * 1 - Entering
     * 2 - Outing
     */
    private byte state = 0;
    //endregion

    //region Emotional Services
    private Service<Void> emotionDetectService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("-------FACE_INFORMATION-------");
                    Platform.runLater(() -> lbl_emotionVal.setText("Processing..."));
                    if (imgFont.getImage() != null) {
                        detecedEmotion = EmotionDetector.getInstance().getEmotion(imgFont.getImage());
                    } else {
                        System.err.println("EmoDectectTask: null front img");
                        detecedEmotion = EnumEmotion.ERROR;
                    }
                    Platform.runLater(() -> {
                        lbl_emotionVal.setText(detecedEmotion.toString());
                        lbl_emotionVal.setStyle("-fx-text-fill: " + detecedEmotion.getColorRelate());
                    });
                    System.out.println("--------END_INFORMATION-------");
                    return null;
                }
            };
        }
    };
    //endregion

    //region Image Processing
    private ScheduledExecutorService timer;
    private int deviceIndex = 0;
    private VideoCapture capture;
    private Mat frame;
    private Mat m1;
    private int focusWidth, focusHeight, focusX, focusY;


    private CameraStreamer cameraStreamer;
    private CameraStreamer cameraStreamer2;
    //endregion
    //endregion

    //region Methods

    //region Initialization
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeToWaitingMode();

        txtRFID.textProperty().addListener((observable, oldValue, newValue) -> {
            // Real purpose
            if (RFIDHandler.checkValidRFID(newValue.toUpperCase())) {
                Platform.runLater(() -> {
                    enterOutBtn.setDisable(false);
                    enterOutBtn.fire();
                });
            } else {
                Platform.runLater(() -> {
                    enterOutBtn.setDisable(true);
                });
            }
//            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });
    }
    //endregion

    //region Gui Helper
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

    public void setTextRFID(String textRFID) {
        this.txtRFID.setText(textRFID);
    }

    private void emotionLbl_changeState(EnumEmotion enumEmo) {
        lbl_emotionVal.setText(enumEmo.toString());
        lbl_emotionVal.setStyle("-fx-text-fill: " + enumEmo.getColorRelate() + ";");
    }

    private void resetImg() {
        Platform.runLater(() -> {
            imgBack.setImage(defaultImg);
            imgFont.setImage(defaultImg);
            imgPlate.setImage(defaultImg_plate);
        });
    }
    //endregion


    //region State Helper

    public void changeToWaitingMode() {
        state = 0;
        enterOutBtn_changeToYellow();

        resetImg();
        currentParkingSession = null;
        Platform.runLater(() -> {
            txtPlateNumber.setDisable(true);
            txtRFID.setDisable(false);
            btn_cancel.setDisable(true);

            txtPlateNumber.setText("");
            txtRFID.setText("");

            lbl_checkInTime.setText("-");
            lbl_checkOutTime.setText("-");
            lbl_parkingDuration.setText("-");
            lbl_emotionVal.setText("-");
            lbl_parkingFee.setText("- VND");

            lbl_emotionVal.setStyle("-fx-text-fill: #212121");

            try {
                emotionDetectService.reset();
            } catch (IllegalStateException e) {
                System.out.println("ERROR Emotion");
            }
        });
    }

    public void changeToConfirm_EnterMode() {
        state = 1;
        enterOutBtn_changeToGreen();
        txtPlateNumber.setDisable(false);
        txtRFID.setDisable(true);
        btn_cancel.setDisable(false);

        currentParkingSession = new ParkingSession(txtRFID.getText(), null, null, null, new Date());

        timeIn = currentParkingSession.getTimeIn();
        Platform.runLater(() -> {
//            imgFont.setImage(imgCamFont.getImage());
//            imgBack.setImage(imgCamBack.getImage());
//            imgPlate.setImage(imgCamPlate.getImage());

            lbl_checkInTime.setText(MainProgram.getSimpleDateFormat().format(timeIn));
            lbl_parkingDuration.setText("0");
            lbl_parkingFee.setText("0 VND");

            emotionDetectService.start();
        });
    }

    public void changeToConfirm_OutMode() {
        state = 2;
        enterOutBtn_changeToRed();
        txtPlateNumber.setDisable(true);
        txtRFID.setDisable(true);
        btn_cancel.setDisable(false);

        timeIn = currentParkingSession.getTimeIn();
        timeOut = new Date();

        long duration = getDateDiff(currentParkingSession.getTimeIn(), timeOut, TimeUnit.HOURS);

        Platform.runLater(() -> {
            imgFont.setImage(currentParkingSession.getFrontImg());
            imgBack.setImage(currentParkingSession.getBackImg());
            imgPlate.setImage(currentParkingSession.getPlateImg());

            txtPlateNumber.setText(currentParkingSession.getPlateNumber());
            lbl_checkInTime.setText(MainProgram.getSimpleDateFormat().format(timeIn));
            lbl_checkOutTime.setText(MainProgram.getSimpleDateFormat().format(timeOut));
            lbl_parkingDuration.setText(duration + " Hours");
            lbl_parkingFee.setText(SessionParkingServices.getInstance().calculateParkingFee(duration) + " VND");

            emotionDetectService.start();
        });
    }


    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    //endregion

    //region JavaFx
    @FXML
    void enterOutBtn_onAction(ActionEvent event) {
        if (state == 0) {
            System.out.println(this.name + " input RFID: " + txtRFID.getText());
            enterOutBtn.setText("...");
            currentParkingSession = SessionParkingServices.getInstance().getParkingSessionByRfidFromParkingList(txtRFID.getText());
            if (currentParkingSession != null) {
                if (role == 0 || role == 2) {
                    changeToConfirm_OutMode();
                } else {
                    changeToWaitingMode();
                }
            } else {
                if (role == 0 || role == 1) {
                    changeToConfirm_EnterMode();
                } else {
                    changeToWaitingMode();
                }
            }
        } else if (state == 1) {
            currentParkingSession.setPlateNumber(txtPlateNumber.getText());
            currentParkingSession.setFrontImg(imgFont.getImage());
            currentParkingSession.setBackImg(imgBack.getImage());
            currentParkingSession.setPlateImg(imgPlate.getImage());
            currentParkingSession.setEmotionIn(detecedEmotion);
            SessionParkingServices.getInstance().addParkingSession(currentParkingSession);
            System.out.println(currentParkingSession + " IN");
            enterOutBtn.setText("...");
            changeToWaitingMode();
        } else if (state == 2) {
            currentParkingSession.changeStatusToLeft();
            currentParkingSession.setEmotionOut(detecedEmotion);
            SessionParkingServices.getInstance().moveParkingSessionToOtherList(currentParkingSession);
            System.out.println(currentParkingSession + " OUT");
            enterOutBtn.setText("...");
            changeToWaitingMode();
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
            ImageProcessing.setImage(imgBack, packet.getOriginMat());
            ImageProcessing.setImage(imgPlate, packet.getDetectedPlate());
            txtPlateNumber.setText(packet.getLicenseNumber());
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        System.out.println("Reach here");
        Platform.exit();
    }


    @FXML
    void onTestAll(ActionEvent event) {
//		demoProcessAllImgs();
//		ImageProcessing.train();

//		Platform.runLater(() -> {
        cameraStreamer = new CameraStreamer(0, imgCamFont);
        cameraStreamer2 = new CameraStreamer(0, imgCamBack);
//
//            cameraStreamer.setFps(50);
//            cameraStreamer2.setFps(50);

        cameraStreamer.startStream();
        cameraStreamer2.startStream();
//		});

        //Thread thread = new Thread(new CameraStreamer(0, imgCamFont));
//		streamVideo();
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
            stage.setTitle(this.name + " configurate");
            stage.show();
//            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    //endregion

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


    //region Getter - Setter
    public void setRole(byte newRole) {
        this.role = newRole;
        switch (role) {
            case 0:
                System.out.println(this.name + " Change to both enter and out lane");
                break;
            case 1:
                System.out.println(this.name + " Change to only enter lane");
                break;
            case 2:
                System.out.println(this.name + " Change to only out lane");
                break;
        }
    }

    public byte getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setFocusConfig(int width, int height, int x, int y) {
        focusWidth = width;
        focusHeight = height;
        focusX = x;
        focusY = y;
    }
    //endregion

}

//endregion





