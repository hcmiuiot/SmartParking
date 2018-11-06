package main;

import com.google.gson.Gson;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;

public class DataPacket {
    public DataPacket(Mat originMat) {
        this.originMat = originMat;
        ImageProcessing.recognizeLicenseNumber(this);
    }
    public DataPacket(File originImgFileName) {
        this(Imgcodecs.imread(originImgFileName.getAbsolutePath()));
    }

    private Mat originMat;
    private boolean isRecognized = false;
    private Mat detectedPlateMat;
    private String licenseNumber;

    public Mat getOriginMat() { return originMat; }
    public void setOriginMat(Mat originMat) { this.originMat = originMat; }

    public Mat getDetectedPlate() {return detectedPlateMat; }
    public void setDetectedPlate(Mat detectedPlate) { this.detectedPlateMat = detectedPlate; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public boolean isRecognized() { return isRecognized; }
    public void setRecognized(boolean recognized) { isRecognized = recognized; }


    @Override
    public String toString() {
        Gson json = new Gson();
        return json.toJson(this);
    }
}
