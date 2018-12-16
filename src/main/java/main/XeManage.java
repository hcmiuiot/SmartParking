package main;

import java.text.ParseException;
import java.util.ArrayList;

public class XeManage {
    private static XeManage instance;
    private static ArrayList<Xe> listOfOtherXe = new ArrayList<>();
    private static ArrayList<Xe> listOfParkingXe = new ArrayList<>();

    private XeManage() {
    }

    //Thread Safe getInstance
    public static XeManage getInstance() {
        if (instance == null) {
            synchronized (XeManage.class) {
                if (null == instance) {
                    instance = new XeManage();
                }
            }
        }
        return instance;
    }

    public static Boolean isExistRfidFromParkingList(String RFIDNumber) {
        for (int i = listOfParkingXe.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that Xe is parking
            if (listOfParkingXe.get(i).getRfidNumber().toUpperCase().equals(RFIDNumber.toUpperCase()) && listOfParkingXe.get(i).getStatus() == true)
                return true;
        }
        return false;
    }

    public static Xe getXeByRfidFromParkingList(String RFIDNumber) {
        for (int i = listOfParkingXe.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that Xe is parking
            if (listOfParkingXe.get(i).getRfidNumber().toUpperCase().equals(RFIDNumber.toUpperCase()) && listOfParkingXe.get(i).getStatus() == true)
                return listOfParkingXe.get(i);
        }
        return null;
    }

    public static long calculateParkingFee(Long duration) {
        return duration * Constants.FEE_PER_HOUR;
    }

    public void moveXeToOtherList(Xe xe){
        for (int i = listOfParkingXe.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that Xe is parking
            if (listOfParkingXe.get(i).getRfidNumber().toUpperCase().equals(xe.getRfidNumber().toUpperCase()) && listOfParkingXe.get(i).getStatus() == true){
                listOfOtherXe.add(listOfParkingXe.get(i));
                listOfParkingXe.remove(i);
                return;
            }
        }
    }

    public static void addXe(Xe newXe) {
        listOfParkingXe.add(newXe);
    }

}
