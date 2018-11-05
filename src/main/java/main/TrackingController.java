package main;

import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TrackingController implements Initializable {
	
	@FXML
	private ImageView imgCamera;
	@FXML
    private ImageView imgPlate;
	@FXML
    private JFXTextField txtPlateNumber;

	private ScheduledExecutorService timer;
	private int deviceIndex = 0;
	private VideoCapture capture;
	private Mat frame;
	private Mat m1;
	double lowRatio = 0f;
	double highRatio = 4f;
	double minArea = 1000;
	double maxArea = 2000;
	double lowWhiteDensity = 1000;
	double highWhiteDensity = 3000;

	public void initialize(URL location, ResourceBundle resources) {
		setDeviceIndex(0);
	    //startLiveVideo();
		// stopLiveVideo();
	}

	@FXML
	void onTest(ActionEvent event) {
		//streamVideo();
//		ImageProcessing.train("D:\\IT\\IdeaProjects\\SmartParking\\data\\demo\\char", "D:\\IT\\IdeaProjects\\SmartParking\\trainedData.txt");
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("./data/img"));
		File img = chooser.showOpenDialog(null);
		if (img != null)
			ImageProcessing.setImage(imgCamera, )
			txtPlateNumber.setText(ImageProcessing.getLicensePlateNumber(img));
	}
	
	@FXML
    void onTestAll(ActionEvent event) {
//		demoProcessAllImgs();
		ImageProcessing.train();
//		streamVideo();
    }
	
	
	private void demoProcessAllImgs() {
		File folder = new File("./img");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		        
		        ImageProcessing.getLicensePlateNumber(listOfFiles[i]);
		        
		      }
		        
//		      } else if (listOfFiles[i].isDirectory()) {
//		        System.out.println("Directory " + listOfFiles[i].getName());
//		      }
		      
		    }
	}
	
	
	VideoCapture vc;
	Mat a;
	public void streamVideo() {
		vc = new VideoCapture("C:\\Users\\Thuan NG\\Desktop\\20180608_123824.mp4");
//		vc = new VideoCapture(0);
		a = new Mat();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while (vc.isOpened()) {
					
					vc.read(a);
//					try {
//						TimeUnit.MILLISECONDS.sleep(50);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					ImageProcessing.getLicensePlateNumber(a);
					try {
						TimeUnit.MILLISECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();
	}


	@FXML
	private void onConfig(ActionEvent event) {
		FXMLLoader configLoader = new FXMLLoader(getClass().getResource("../../resources/TrackingConfigForm.fxml"));
		try {
			AnchorPane configDialog = configLoader.load();
			Stage stage = new Stage();
			stage.setResizable(false);
			stage.setScene(new Scene(configDialog));
			TrackingConfigController configController = configLoader.getController();
			configController.setTrackingController(this);
			stage.show();

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

	private void setDeviceIndex(int deviceIdx) {
		this.deviceIndex = deviceIdx;
	}
	
	public int getDeviceIndex() {
		return this.deviceIndex;
	}

	private void startLiveVideo() {
		capture = new VideoCapture(deviceIndex);

		// frame = new Mat();
		// frame = Imgcodecs.imread("./img/2.jpg", Imgcodecs.IMREAD_COLOR);
		// HighGui.windows.put("a", new ImageWindow("a", 0));
		// HighGui.imshow("a", frame);
		// HighGui.waitKey();
		// System.out.println(frame);
		// m1 = frame.clone();

		new Thread(() -> {
			while (capture.isOpened()) {
//				 capture.read(frame);
//				 m1 = frame.clone();
				frame = Imgcodecs.imread("./img/1.jpg", Imgcodecs.IMREAD_COLOR);
				Imgproc.resize(frame, frame, new Size(800, 600));

				m1 = frame.clone();
				Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

				Imgproc.adaptiveThreshold(frame, frame, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 135,
						2);

				

				List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				Mat hierarchy = new Mat();
				Imgproc.findContours(frame, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
				// Imgproc.adaptiveThreshold(frame, frame, 255,
				// Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 3f);
				for (MatOfPoint contour : contours) {
					Rect rec = Imgproc.boundingRect(contour);
					float ratio = 1f * rec.width / rec.height;
					// if (ratio > 3.8f )
					// Imgproc.putText(m1, ""+ratio, rec.tl(), 0, 0.4f, new Scalar(0,255,0), 2);

					if (ratio >= 3.8f && ratio <= 4.9f) {
						double wholeRatio = rec.area() / (frame.width() * frame.height());

						double white = (1f + Core.countNonZero(frame.submat(rec))) / rec.area();
						if (rec.area() > 500 && white <= .72f && white >= .5f) {
							Imgproc.putText(m1, "" + wholeRatio, rec.tl(), 0, 1f, new Scalar(0, 255, 0), 2);
							Imgproc.rectangle(m1, rec.tl(), rec.br(), new Scalar(0, 0, 255));
						}
					}
				}

				Platform.runLater(() -> {
					Imgproc.rectangle(m1, new Point(focusX, focusY),
							new Point(focusX + focusWidth, focusY + focusHeight), new Scalar(0, 0, 255));
					imgCamera.setImage(ImageProcessing.mat2Image(frame));
				});
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void stopLiveVideo() {
		capture.release();
		timer.shutdown();
	}

}


