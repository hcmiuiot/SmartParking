package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;

import javafx.scene.image.Image;

public class ImageProcessing {

	private static ImageProcessing instance;
	
	private static final int SZ = 20;
	
	private ImageProcessing() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	public static ImageProcessing getInstance() {
		if (instance == null) {
			instance = new ImageProcessing();
		}
		return instance;
	}
	
	public static Image mat2Image(Mat img) {
		MatOfByte byteMat = new MatOfByte();
		Imgcodecs.imencode(".bmp", img, byteMat);
		System.out.println(img);
		return new Image(new ByteArrayInputStream(byteMat.toArray()));
	}
	public static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {        
	    MatOfByte mob = new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    byte ba[] = mob.toArray();

	    BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
	    return bi;
	}
	public static ImageView setImage(ImageView imgView, Mat img) {
		if (imgView != null && img != null)
			Platform.runLater(() -> {
				imgView.setImage(ImageProcessing.mat2Image(img));
			});
		return imgView;
	}

	//TAG: USELESS
	public static Mat deskew(Mat img) {
		Moments m = Imgproc.moments(img);
		if (Math.abs(m.get_mu02()) < 1e-2) {
			return img;
		}
		double skew = 1f*m.get_mu11()/m.get_mu02();

		Mat map = new Mat(CvType.CV_32F);
		map.push_back(new MatOfFloat(1f, (float)skew, (float)(-0.5f*SZ*skew)));
		map.push_back(new MatOfFloat(0f,1f,0f));

		Mat result = new Mat();
		Imgproc.warpAffine(img, result, map, img.size(), Imgproc.WARP_INVERSE_MAP | Imgproc.INTER_LINEAR);

		return img;
	}
	
	private static SVM svm4training;
	private static SVM svm4prediction = null;

	private static final Size PREDICTION_CHAR_SIZE = new Size(10,10);
	private static final String TRAIN_CHARS_DIR = ".data/demo/char";
	private static final String TRAIN_OUTPUT_DIR = "./trainedData.txt";
	private static final Size PREPROCESSING_RESIZE_SIZE = new Size(800, 600);
	private static final int THRESHOLD_BLOCK_SIZE = 95;
	private static final int THRESHOLD_C = 0;
	private static final int MORPHOLOGY_SIZE = 2;
	
	public static HOGDescriptor getHog() {
		HOGDescriptor hog = new HOGDescriptor(PREDICTION_CHAR_SIZE, new Size(10,10), new Size(5,5), new Size(5,5), 9, 1, -1, 0, 0.2f, true, 64, true);
		return hog;
	}

	public static void train(String charactersDirectory, String outputFileName) {
		svm4training = SVM.create();

		svm4training.setType(SVM.C_SVC);
		svm4training.setKernel(SVM.RBF);
		svm4training.setC(16);
//	    svm4training.setC(50);
//	    svm4training.setGamma(0.50625);
		svm4training.setGamma(0.5);
		svm4training.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 100, 1e-6));

		Mat samples = new Mat();
		Mat labels = new Mat();

		File folder = new File(charactersDirectory);
		File[] listOfFiles = folder.listFiles();
		System.out.println(listOfFiles);

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				String charCode =  listOfFiles[i].getName();
				System.out.println("Directory " + charCode);
				File subFolder = new File(charactersDirectory +"\\"+ charCode);
		    		
				File[] listOfImages = subFolder.listFiles();
		    		
				for (int x = 0; x < listOfImages.length; x++) {
					if (listOfImages[x].isFile()) {
						//System.out.println(listOfImages[x].getPath()+"/" + list);
						Mat m = Imgcodecs.imread(listOfImages[x].getPath(), 0);
						Imgproc.resize(m, m, PREDICTION_CHAR_SIZE);
//						Imgproc.threshold(m, m, 50, 255, Imgproc.THRESH_BINARY);
						//m = m.reshape(1,1);
						System.out.println(m);

						HOGDescriptor hog = getHog();

						//Calculate HOG descriptors
						MatOfFloat descriptors = new MatOfFloat();
						hog.compute(m, descriptors);
		    				
						Mat descriptors32f1 = new Mat();
						descriptors.convertTo(descriptors, CvType.CV_32FC1);
						descriptors.copyTo(descriptors32f1);

						descriptors32f1 = descriptors32f1.reshape(1, 1);

						//descriptors = (MatOfFloat) descriptors.reshape(1,1);
						System.out.println(descriptors32f1.dump());
						//Imgproc.cvtColor(m, m, Imgproc.COLOR_BGR2GRAY);

						samples.push_back(descriptors32f1);
						labels.push_back(new MatOfInt(charCode.charAt(0)));
					}
				}
		      }
		}
		
//		System.out.println(samples.rows());
//		System.out.println(labels);
		//svm4training.train(samples, Ml.ROW_SAMPLE, labels);
		svm4training.trainAuto(samples, Ml.ROW_SAMPLE, labels);
		svm4training.save(outputFileName);
		System.out.println("TRAINING COMPLETED!");
	}
	public static void train(){ train(TRAIN_CHARS_DIR, TRAIN_OUTPUT_DIR); }

	public static int predictChar(Mat img, String trainedFilename) {
		Imgproc.resize(img, img, PREDICTION_CHAR_SIZE);
//		Imgproc.threshold(img, img, 50, 255, Imgproc.THRESH_BINARY);
//		img = img.reshape(1,1);
//		img.convertTo(img, CvType.CV_32FC1);
		
		MatOfFloat hog = new MatOfFloat();
		Mat mat4prediction = new Mat();
		
		getHog().compute(img, hog);

		hog.copyTo(mat4prediction);
		
		mat4prediction = mat4prediction.reshape(1,1);
		mat4prediction.convertTo(mat4prediction, 5);
		
		if (svm4prediction == null)
			svm4prediction = SVM.load(trainedFilename);
		
		return (int)svm4prediction.predict(mat4prediction);
	}
	public static int predictChar(Mat img) { return predictChar(img, TRAIN_OUTPUT_DIR); }

	public static Mat preprocessingImg(Mat srcImg) {
		Mat frame = srcImg.clone();

		//Resize image
		Imgproc.resize(frame, frame, PREPROCESSING_RESIZE_SIZE);

		Mat grayImg = new Mat();
		//Change to gray photo
		Imgproc.cvtColor(frame, grayImg, Imgproc.COLOR_BGR2GRAY);

		Mat thresholdedImg = new Mat();
		//Adaptive threshold
		Imgproc.adaptiveThreshold(grayImg, thresholdedImg, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
				Imgproc.THRESH_BINARY, THRESHOLD_BLOCK_SIZE, THRESHOLD_C);

		Mat morphology = new Mat();
		//Morphology
		Imgproc.morphologyEx(thresholdedImg, morphology, Imgproc.MORPH_OPEN,
				Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(MORPHOLOGY_SIZE,MORPHOLOGY_SIZE)));

		return morphology;
	}
	public static Mat detectPlate(Mat processedImg) {

		//Find contour plate
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(processedImg, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);

		hierarchy = hierarchy.clone().reshape(1);
		int[] childCount = new int[contours.size()];
		Arrays.fill(childCount, 0);

		for (int h = 0; h < contours.size(); h++) {
			int parentIdx = (int) hierarchy.get(0, 4 * h + 3)[0];
			if (parentIdx != -1) {
				// Imgproc.drawContours(frame, contours, h, new Scalar(0,0,255));
				Rect charBoundRect = Imgproc.boundingRect(contours.get(h));
				double charRatio = 1f * charBoundRect.height / charBoundRect.width;

				if (!((charRatio >= 1.4f && charRatio <= 2.8f) ||
						(charRatio >= 2.8f && charRatio <= 3.9f)))
					continue;
				//if (charRatio < 1.55f) continue;
				if (charBoundRect.area() < 150) continue;

				childCount[parentIdx] = childCount[parentIdx] + 1;

				//Pass plate having more than 7 characters
				if (childCount[parentIdx] >= 7) {// && childCount[parentIdx] <= 30 ) {
					// Imgproc.drawContours(frame, contours, parentIdx, new Scalar(255,255,0));

					//Calculate licence plate ratio
					Rect r = Imgproc.boundingRect(contours.get(parentIdx));
					double plateRatio = 1f * r.width / r.height;
//					System.out.println("Child count = " + childCount[parentIdx]);
					if (r.area() < 3000f)
						continue;

//					System.out.println("PLATE RATIO = " + plateRatio);

					boolean isSquarePlate = (plateRatio > 1.03f && plateRatio < 1.52f);
					boolean isRectPlate = (plateRatio > 2.91f && plateRatio < 4.85f);

					//Exit if not a plate
					//Dimension ratio filter
					if (!(isSquarePlate || isRectPlate))
						continue;

					r.x = r.x - 5;
					r.y = r.y - 5;
					r.width += 10;
					r.height += 10;

					//Not touch to the edges
					if (r.x <= 0 || r.y <= 0)
						continue;

					//Get ROI
					Mat v = new Mat(processedImg, r);

					//Calculate white density of ROI
					double whiteDensity = Core.countNonZero(v) / r.area();

//					System.out.println("WHITE DENSITY = " + whiteDensity);

					//White density filter
					if (whiteDensity < 0.35f || whiteDensity > 0.75f)
						continue;

					RotatedRect rr = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(parentIdx).toArray()));
					MatOfPoint points = new MatOfPoint();

					//Imgproc.approxPolyDP(curve, approxCurve, epsilon, closed);
					Imgproc.boxPoints(rr, points);
					Point[] pointss = new Point[4];
					rr.points(pointss);

//					Mat aa = processedImg.clone();
//					Imgproc.line(aa, pointss[0], pointss[1], new Scalar(0, 255, 0), 2);
//					Imgproc.line(aa, pointss[1], pointss[2], new Scalar(0, 255, 0), 2);
//					Imgproc.line(aa, pointss[2], pointss[3], new Scalar(0, 255, 0), 2);
//					Imgproc.line(aa, pointss[3], pointss[0], new Scalar(0, 255, 0), 2);

					MatOfPoint2f res = new MatOfPoint2f();
					MatOfPoint2f des = new MatOfPoint2f();

					res.push_back(new MatOfPoint2f(pointss[0]));
					res.push_back(new MatOfPoint2f(pointss[1]));
					res.push_back(new MatOfPoint2f(pointss[2]));
					res.push_back(new MatOfPoint2f(pointss[3]));
//
					int desWidth = 470;
					int desHeight = 300;

					if (isRectPlate) {
						desWidth = 470;
						desHeight = 110;
					}
//
					if (isSquarePlate) {
						desWidth = 280;
						desHeight = 200;
					}

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

					Mat transformMatrix = Imgproc.getPerspectiveTransform(res, des);

					Mat plateImg = new Mat();
					Imgproc.warpPerspective(processedImg, plateImg, transformMatrix, new Size(desWidth, desHeight));

					Imgproc.threshold(plateImg, plateImg, 50, 255, Imgproc.THRESH_BINARY);
					Imgproc.morphologyEx(plateImg, plateImg, Imgproc.MORPH_OPEN,
							Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(MORPHOLOGY_SIZE,MORPHOLOGY_SIZE)));
					return plateImg;
				}
			}
		}
		return null;
	}
	public static ArrayList<CharacterBox> getCharactersFromPlate(Mat plateImg) {
		//Find character contours
		ArrayList<MatOfPoint> plateContours = new ArrayList<>();
		Mat plateHierarchy = new Mat();
		Imgproc.findContours(plateImg, plateContours, plateHierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);

		ArrayList<CharacterBox> characterBoxs = new ArrayList<>();
		for (MatOfPoint plateContour : plateContours) {
			Rect charRec = Imgproc.boundingRect(plateContour);
			double charRatio2 = 1f * charRec.height / charRec.width;

			//Set character ROI
			Mat charMat = plateImg.submat(charRec);
			double white = 1f * Core.countNonZero(charMat) / charRec.area();

			//Character ratio filter
			if (charRatio2 >= 1.4f && charRatio2 <= 5.4f &&
					white > 0.3f && white < 0.8f) {
				double areaPercent = 1f * charRec.area() / (plateImg.width() * plateImg.height());
				if (areaPercent >= 0.02f) {
					characterBoxs.add(new CharacterBox(charMat, charRec.x, charRec.y));
					Collections.sort(characterBoxs);
				}
			}
		}
		return characterBoxs;
	}
	public static String OCRCharacters(ArrayList<CharacterBox> characterBoxes) {
		String plateNumber = "";
		for (int t = 0; t < characterBoxes.size(); t++) {

//			System.out.println(characterBoxes.get(t).getMat());

			//Imgcodecs.imwrite("./demo/char/" + characterBoxes.get(t).hashCode() + ".jpg", characterBoxes.get(t).getMat());

			plateNumber += (char) predictChar(characterBoxes.get(t).getMat());
		}
		return plateNumber;
	}

	public static String getLicensePlateNumber(Mat img) {
		Mat preprocessedImg = preprocessingImg(img);
		Mat plate = detectPlate(preprocessedImg);
		ArrayList<CharacterBox> characterBoxes = getCharactersFromPlate(plate);
		String licenseNumber = OCRCharacters(characterBoxes);

		System.out.println("Plate number: " + licenseNumber);

		//Imgcodecs.imwrite("./demo/"+frame.hashCode(), plateColor);
		return licenseNumber;

// 		HighGui.waitKey();
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
	public static String getLicensePlateNumber(File imgFileName) {
		return getLicensePlateNumber(Imgcodecs.imread(imgFileName.getAbsolutePath()));
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


