package main;

import Camera.CameraStreamer;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;


public class TrackingController {

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

    private ScheduledExecutorService timer;
    private int deviceIndex = 0;
    private VideoCapture capture;
    private Mat frame;
    private Mat m1;

    //public void initialize(URL location, ResourceBundle resources) {
    //setDeviceIndex(0);
    //startLiveVideo();
    // stopLiveVideo();
    //}

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

    public void setTextRFID(String textRFID){
        this.txtRFID.setText(textRFID);
    }

//	@Override
//	public void finalizeStream() throws IOException {
//		shutdown();
//	}
}


