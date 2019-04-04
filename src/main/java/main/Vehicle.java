package main;

import javafx.scene.image.Image;

import java.util.Date;

public class Vehicle {
    private String rfidNumber;
    private Image frontImg;
    private Image backImg;
    private Image plateImg;
    private String plateNumber;
    private Boolean status; // 1 - parking ; 0 - left
    private Date timeIn;
    private Date timeOut;
    private EnumEmotion emotionIn = EnumEmotion.UNKNOWN;
    private EnumEmotion emotionOut = EnumEmotion.UNKNOWN;

    public Vehicle(String rfidNumber, Image frontImg, Image backImg, String plateNumber, Date timeIn) {
        this.rfidNumber = rfidNumber;
        this.frontImg = frontImg;
        this.plateImg = backImg;
        this.plateNumber = plateNumber;
        this.timeIn = timeIn;
        this.status = true;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(MainProgram.getSimpleDateFormat().format(timeIn)).append(", ");
        builder.append(rfidNumber).append(", ");
        builder.append(plateNumber).append(" ");
        return builder.toString();
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
    public void changeStatusToLeft(){
        this.status = false;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Image getBackImg() {
        return backImg;
    }

    public void setBackImg(Image backImg) {
        this.backImg = backImg;
    }

    public EnumEmotion getEmotionIn() { return emotionIn; }

    public void setEmotionIn(EnumEmotion emotionIn) { this.emotionIn = emotionIn; }

    public EnumEmotion getEmotionOut() { return emotionOut; }

    public void setEmotionOut(EnumEmotion emotionOut) { this.emotionOut = emotionOut; }
}
