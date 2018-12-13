package main;

import javafx.scene.image.Image;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Xe {
    private String rfidNumber;
    private Image frontImg;
    private Image plateImg;
    private String plateNumber;
    private Boolean status;
    private Date timeIn;
    private String timestampInString;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Xe(String rfidNumber, Image frontImg, Image plateImg, String plateNumber, Date timeIn) {
        this.rfidNumber = rfidNumber;
        this.frontImg = frontImg;
        this.plateImg = plateImg;
        this.plateNumber = plateNumber;
        this.timeIn = timeIn;
        this.timestampInString = sdf.format(timeIn);
    }

    public Image getFrontImg() {
        return frontImg;
    }

    public void setFrontImg(Image frontImg) {
        this.frontImg = frontImg;
    }

    public Image getPlateImg() {
        return plateImg;
    }

    public void setPlateImg(Image plateImg) {
        this.plateImg = plateImg;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public String getRfidNumber() {
        return rfidNumber;
    }

    public void setRfidNumber(String rfidNumber) {
        this.rfidNumber = rfidNumber;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
