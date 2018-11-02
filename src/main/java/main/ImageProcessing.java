package main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
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
		return new Image(new ByteArrayInputStream(byteMat.toArray()));
	}
	
	public static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {        
	    MatOfByte mob = new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    byte ba[] = mob.toArray();

	    BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
	    return bi;
	}
	
	static SVM svm;
	
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
	
	private static final Size charSize = new Size(10,10);
	
	public static HOGDescriptor getHog() {
		HOGDescriptor hog = new HOGDescriptor(charSize, new Size(10,10), new Size(5,5), new Size(5,5), 9, 1, -1, 0, 0.2f, true, 64, true);
		//MatOfFloat descriptors = new MatOfFloat();
		return hog;
	}
	
	
	public static void train() {
		svm = SVM.create();
		
		svm.setType(SVM.C_SVC);
	    svm.setKernel(SVM.RBF);
	    svm.setC(16);
//	    svm.setC(50);
//	    svm.setGamma(0.50625);
	    svm.setGamma(0.5);
	    svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 100, 1e-6));
		
		Mat samples = new Mat();
		Mat labels = new Mat();
		
		
		File folder = new File("./demo/char");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		    	if (listOfFiles[i].isDirectory()) {
		    		String charCode =  listOfFiles[i].getName();
		    		System.out.println("Directory " + charCode);
		    		File subFolder = new File("./demo/char/" + charCode);
		    		
		    		File[] listOfImages = subFolder.listFiles();
		    		
		    		for (int x = 0; x < listOfImages.length; x++) {
		    			if (listOfImages[x].isFile()) {
		    				
		    				//System.out.println(listOfImages[x].getPath()+"/" + list);
		    				Mat m = Imgcodecs.imread(listOfImages[x].getPath(), 0);
		    				Imgproc.resize(m, m, charSize);
		    				Imgproc.threshold(m, m, 50, 255, Imgproc.THRESH_BINARY);
		    				//m = m.reshape(1,1);
		    				System.out.println(m);
		    				//m.convertTo(m, CvType.CV_32FC1);
		    				
		    				HOGDescriptor hog = getHog();
		    				
		    				MatOfFloat descriptors = new MatOfFloat(); 				
		    				
		    				hog.compute(m, descriptors);
		    				
		    				Mat des2 = new Mat();
		    				descriptors.convertTo(descriptors, CvType.CV_32FC1);
		    				descriptors.copyTo(des2);
		    				
		    				des2 = des2.reshape(1, 1);
		    				
		    				
		    				//descriptors = (MatOfFloat) descriptors.reshape(1,1);
		    				System.out.println(des2.dump());
		    				//Imgproc.cvtColor(m, m, Imgproc.COLOR_BGR2GRAY);
		    				
		    				//descriptors = (MatOfFloat) descriptors.reshape(1,1);
		    				
		    				
		    				//m.convertTo(m, CvType.CV_32FC1);
		    				
		    				samples.push_back(des2);
		    				labels.push_back(new MatOfInt(charCode.charAt(0)));
//		    				HighGui.imshow(""+m.hashCode(), m);
//		    				HighGui.moveWindow(""+m.hashCode(), x*100, x*80);
//		    				
//		    				HighGui.waitKey(100);
		    			}
		    		}
		    		
		      }
	
		}
		
		System.out.println(samples.rows());
		System.out.println(labels);
		svm.train(samples, Ml.ROW_SAMPLE, labels);
		//svm.trainAuto(samples, Ml.ROW_SAMPLE, labels);
		svm.save("./trainedDataHOG.txt");
	}
	
	static SVM svm2 = null;
	
	public static int predictForDuong(Mat img) {
		Imgproc.resize(img, img, charSize);
		Imgproc.threshold(img, img, 50, 255, Imgproc.THRESH_BINARY);
		//img = img.reshape(1,1);
		//img.convertTo(img, CvType.CV_32FC1);
		
		MatOfFloat hogg = new MatOfFloat();
		Mat a = new Mat();
		
		getHog().compute(img, hogg);
		//getHog().setSVMDetector(svm2.getSupportVectors());
		//getHog().detectMultiScale(img, foundLocations, foundWeights, hitThreshold, winStride, padding, scale, finalThreshold, useMeanshiftGrouping);
		hogg.copyTo(a);
		
		a = a.reshape(1,1);
		a.convertTo(a, 5);
		
		if (svm2 == null)
			svm2 = SVM.load("./trainedDataHOG.txt");
		
		System.out.println(svm2.getVarCount());
		
		return (int)svm2.predict(a);
	}
	
//	public static int predictForDuong(String imgDir) {
//		Mat img = Imgcodecs.imread(imgDir, 0);
//		Imgproc.resize(img, img, new Size(40,70));
//		img = img.reshape(1,1);
//		img.convertTo(img, CvType.CV_32FC1);
//		
//		if (svm2 == null)
//			svm2 = SVM.load("./trainedDataHOG.txt");
//		
//		return (int)svm2.predict(img);
//	}
	
}


