package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

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
		streamVideo();
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("./data/img"));
		File img = chooser.showOpenDialog(null); 
		if (img != null)
			processImg(img);
	}
	
	@FXML
    void onTestAll(ActionEvent event) {
		//ImageProcessing.initSVM();
		//demoProcessAllImgs();
		ImageProcessing.train();
		//ImageProcessing.predictForDuong(new File("‪C:\\Users\\Thuan NG\\Desktop\\Untitled.png"));
		
//		streamVideo();
    }
	
	
	private void demoProcessAllImgs() {
		File folder = new File("./img");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		        
		        processImg(listOfFiles[i]);
		        
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
					processImg(a);
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
	

	private String plateNumber = "";
	
	public void processImg(Mat img) {
		plateNumber = "";

		frame = img.clone();
		
//		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
//		Imgproc.adaptiveThreshold(frame, frame, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 111, 0);
//		
//		HighGui.imshow("frame", frame);
//		HighGui.waitKey(1);
	
		System.out.println("Orginal size: " + frame);

		Mat grayImg = new Mat();
		Mat grayImg2 = new Mat();
		Mat thresholdedImg = new Mat();
		Mat thresholdedImg2 = new Mat();
		Mat morphology = new Mat();

		Imgproc.resize(frame, frame, new Size(800, 600));
		Platform.runLater(() -> {
			imgCamera.setImage(ImageProcessing.mat2Image(frame));
		});
		System.out.println("Resized size: " + frame);
		Imgproc.cvtColor(frame, grayImg, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.equalizeHist(grayImg, grayImg);
		// System.out.println(a);
		//HighGui.imshow("before", grayImg2);
		//Imgproc.GaussianBlur(grayImg2, grayImg2, new Size(0,0), 1);
		//HighGui.imshow("blue", grayImg2);
		//HighGui.waitKey();
//		Imgproc.adaptiveThreshold(grayImg, thresholdedImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,
//				97, 0);
		Imgproc.adaptiveThreshold(grayImg, thresholdedImg2, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
				Imgproc.THRESH_BINARY, 95, 0);

		 Imgproc.morphologyEx(thresholdedImg2, morphology, Imgproc.MORPH_OPEN,
		 Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2,2)));

		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(morphology, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);

		hierarchy = hierarchy.clone().reshape(1);
		int[] childCount = new int[contours.size()];
		Arrays.fill(childCount, 0);

		Mat morphology2 = morphology.clone();
				
		Mat vv = frame.clone();
		for (int h = 0; h < contours.size(); h++) {
			int parentIdx = (int) hierarchy.get(0, 4 * h + 3)[0];
			if (parentIdx != -1) {
				// Imgproc.drawContours(frame, contours, h, new Scalar(0,0,255));
				Rect charBoundRect = Imgproc.boundingRect(contours.get(h));
				double charRatio = 1f*charBoundRect.height/charBoundRect.width;
				
				if ( ((charRatio >= 1.4f && charRatio <= 2.8f) || 
					 (charRatio >= 2.8f && charRatio <= 3.9f)) == false )
					continue;
				//if (charRatio < 1.55f) continue;
				if (charBoundRect.area() < 150) continue;
				
				
//				Imgproc.drawContours(frame, contours, h, new Scalar(0,0,255)); 
//				HighGui.imshow(""+h, vv.submat(charBoundRect));
//				System.out.println("" + h + "R> "+ charRatio);
//				System.out.println("" + h + "A> "+ charBoundRect.area());
//				System.out.println("" + h + "W> "+ charWhiteRatio+" %");
//				HighGui.waitKey();
				
				childCount[parentIdx] = childCount[parentIdx] + 1;
				
				
				if (childCount[parentIdx] >= 7) {// && childCount[parentIdx] <= 30 ) {
					// Imgproc.drawContours(frame, contours, parentIdx, new Scalar(255,255,0));
					Rect r = Imgproc.boundingRect(contours.get(parentIdx));
					double plateRatio = 1f * r.width / r.height;
					System.out.println("Child count = "+ childCount[parentIdx]);
					if (r.area() < 3000f)
						continue;
					
					System.out.println("PLATE RATIO = " + plateRatio);
					
					boolean isSquarePlate = (plateRatio > 1.03f  && plateRatio < 1.52f);
					boolean isRectPlate = (plateRatio > 2.91f && plateRatio < 4.85f);
					
					if ( (isSquarePlate || isRectPlate) == false) 
						continue;
					
					r.x = r.x - 5;
					r.y = r.y - 5;
					r.width  += 10;
					r.height += 10;
					
					System.out.println(r);
					if (r.x<=0 || r.y<=0)
						continue;
					
					
					Mat v = new Mat(morphology, r);
					double whiteDensity =  Core.countNonZero(v) / r.area();
					
					System.out.println("WHITE DENSITY = " + whiteDensity);
					if (whiteDensity < 0.35f || whiteDensity > 0.75f)
						continue;
					
					//Mat showImg = new Mat(frame, r);
					Mat showImg = morphology.submat(r);
					//frame.submat(r).clone().copyTo(showImg);
						
					RotatedRect rr = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(parentIdx).toArray()));
					MatOfPoint points = new MatOfPoint();
					//Imgproc.approxPolyDP(curve, approxCurve, epsilon, closed);
					Imgproc.boxPoints(rr, points);
					Point[] pointss = new Point[4];
					rr.points(pointss);
					
					Mat aa = frame.clone();
					Imgproc.line(aa, pointss[0], pointss[1], new Scalar(0,255,0),2);
					Imgproc.line(aa, pointss[1], pointss[2], new Scalar(0,255,0),2);
					Imgproc.line(aa, pointss[2], pointss[3], new Scalar(0,255,0),2);
					Imgproc.line(aa, pointss[3], pointss[0], new Scalar(0,255,0),2);
					
					MatOfPoint2f res = new MatOfPoint2f();
					MatOfPoint2f des = new MatOfPoint2f();
					//des.push_back(Converters.point);
					res.push_back(new MatOfPoint2f(pointss[0]));
					res.push_back(new MatOfPoint2f(pointss[1]));
					res.push_back(new MatOfPoint2f(pointss[2]));
					res.push_back(new MatOfPoint2f(pointss[3])); 
//					
//					Imgproc.putText(frame, "0", pointss[0], 2, 2, new Scalar(0,0,255));
//					Imgproc.putText(frame, "1", pointss[1], 2, 2, new Scalar(0,0,255));
//					Imgproc.putText(frame, "2", pointss[2], 2, 2, new Scalar(0,0,255));
//					Imgproc.putText(frame, "3", pointss[3], 2, 2, new Scalar(0,0,255));
					
//					HighGui.imshow("FRAME", frame);
//					HighGui.waitKey();
					int desWidth  = 470;
					int desHeight = 300;
					
					if (isRectPlate) {
						desWidth  = 470; 
						desHeight = 110; 					
					}
//					
					if (isSquarePlate) {
						desWidth  = 280;
						desHeight = 200;		
					}
//					
//					if (isRectPlate) {
//						desWidth  = 235; 
//						desHeight = 55; 					
//					}
//
//					if (isSquarePlate) {
//						desWidth  = 140;
//						desHeight = 100;		
//					}
					
					if (pointss[1].y > pointss[3].y) {
						des.push_back(new MatOfPoint2f(new Point(desWidth, desHeight)));
						des.push_back(new MatOfPoint2f(new Point(0f, desHeight)));
						des.push_back(new MatOfPoint2f(new Point(0f, 0f)));
						des.push_back(new MatOfPoint2f(new Point(desWidth, 0f)));						
					} else {
						des.push_back(new MatOfPoint2f(new Point(0f, desHeight)));
						des.push_back(new MatOfPoint2f(new Point(0f, 0f)));
						des.push_back(new MatOfPoint2f(new Point(desWidth, 0f)));
						des.push_back(new MatOfPoint2f(new Point(desWidth, desHeight)));
					}
					
					System.out.println("DEUBG");
							
					Mat mat = Imgproc.getPerspectiveTransform(res, des);
					Mat plate = new Mat();
					Imgproc.warpPerspective(morphology, plate, mat, new Size(desWidth,desHeight));
							
//					System.out.println(mat.dump());
//					HighGui.imshow("a", test);
//					HighGui.waitKey(1);
					
					Imgproc.threshold(plate, plate, 50, 255, Imgproc.THRESH_BINARY);
					
					Platform.runLater(() -> {
						imgPlate.setImage(ImageProcessing.mat2Image(plate));
					});
					
					
					Imgproc.morphologyEx(plate, plate, Imgproc.MORPH_OPEN, 
							Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2,2)));
					
					ArrayList<MatOfPoint> plateContours = new ArrayList<>();
					Mat plateHierarchy = new Mat();
					Imgproc.findContours(plate, plateContours, plateHierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
					
					plateHierarchy = plateHierarchy.clone().reshape(1);
					
					Mat plateColor = plate.clone();
					Imgproc.cvtColor(plate, plateColor, Imgproc.COLOR_GRAY2RGB);

					
					ArrayList<CharacterBox> characterBoxs = new ArrayList<>();
					for (int z = 0; z < plateContours.size(); z++) {
						Rect charRec = Imgproc.boundingRect(plateContours.get(z));
						double charRatio2 = 1f*charRec.height/charRec.width;
						Mat charMat = plate.submat(charRec);
						double white = 1f*Core.countNonZero(charMat)/charRec.area(); 
						
						
						if ( charRatio2 >= 1.4f && charRatio2 <= 5.4f &&
							white > 0.3f && white < 0.8f ) {
//						
							
							double areaPercent = 1f*charRec.area()/(plate.width()*plate.height());

							if (areaPercent >= 0.02f) {
								//Imgproc.putText(plateColor, ""+areaPercent, charRec.tl(), 1, 1, new Scalar(255,0,0));
								Imgproc.rectangle(plateColor, charRec.tl(), charRec.br(), new Scalar(0,0,255));
								//charMat.convertTo(charMat, CvType.CV_32FC1);
								characterBoxs.add(new CharacterBox(charMat, charRec.x, charRec.y));
								Collections.sort(characterBoxs);
								
							}
							
							
						}
//						else
//						Imgproc.putText(plate, String.format("%.2f", charRatio2), charRec.tl(), 1, 1, new Scalar(0,0,255));
//	
					}
					
					
					for (int t=0; t<characterBoxs.size(); t++) {
//						HighGui.imshow(""+t, characterBoxs.get(t).getMat());
//						HighGui.moveWindow(""+t, 100*t, 80*t);
						System.out.println(characterBoxs.get(t).getMat());
						
						Imgcodecs.imwrite("./demo/char/"+characterBoxs.get(t).hashCode()+".jpg", characterBoxs.get(t).getMat());
						
						//plateNumber += (char)ImageProcessing.predictForDuong("./demo/char/"+characterBoxs.get(t).hashCode()+".jpg");
						plateNumber += (char)ImageProcessing.predictForDuong(characterBoxs.get(t).getMat());
						//resString += (char)ImageProcessing.predictForDuong(characterBoxs.get(t).getMat());
					}
//					
					Platform.runLater(()-> {
						txtPlateNumber.setText(plateNumber);
					});
					
					System.out.println("Plate number: " + plateNumber);
					
					//Imgcodecs.imwrite("./demo/"+frame.hashCode(), plateColor);
					
					return;
//					HighGui.imshow("" + parentIdx, v);
//					System.out.println(parentIdx);
//					System.out.println("Area = " + r.area());
//					System.out.println("Ratio = " + ratio);
//					System.out.println("White = " + whiteDensity);
					// HighGui.waitKey();
				}
			}
		}

		//Mat zero = Mat.zeros(new Size(100,100), 1);
		//Imgcodecs.imwrite("./demo/"+frame.hashCode(), zero);
//		Platform.runLater(()-> {
//			txtPlateNumber.setText("");
//		});

		//HighGui.imshow("gray", grayImg);

		//HighGui.imshow("gray2", grayImg2);
		//HighGui.imshow("threshold", thresholdedImg);
//		HighGui.imshow(""+thresholdedImg2.hashCode(), thresholdedImg2);
//		// HighGui.imshow("origin", frame);
//	    HighGui.imshow(""+morphology.hashCode(), morphology);
//		HighGui.waitKey();
		// }
	}
	
	public void processImg(File imgFile) {
		processImg(Imgcodecs.imread(imgFile.getAbsolutePath()));
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

class CharacterBox implements Comparable<CharacterBox>{

	private Mat img;
	private int x,y;
	
	public CharacterBox(Mat img, int x, int y) {
		this.img = img;
		this.x = x;
		this.y = y;
	}
	
	public Mat getMat() {
		return this.img;
	}
	
	@Override
	public int compareTo(CharacterBox o) {
		int minDistance = img.height()/2;
		int deltaY = this.y - o.y;
		if (deltaY <= -minDistance)
			return -1;
		else if (deltaY >= minDistance) 
			return 1;
		else {
			if (this.x < o.x)
				return -1;
			else 
				return 1;
		}
	}
	
}
