package main;

import java.text.ParseException;
import java.util.ArrayList;

public class XeManage {
    private static XeManage instance;
    private static ArrayList<Xe> listOfOtherXe = new ArrayList<>();
    private static ArrayList<Xe> listOfParkingXe = new ArrayList<>();

    private XeManage() {
        try {
            listOfParkingXe.add(new Xe("ABCABCDE", null, null, "63B366659", MainProgram.getSimpleDateFormat().parse("10-12-2018 12:00:24")));
            listOfParkingXe.add(new Xe("12A2A81A", null, null, "79N43225", MainProgram.getSimpleDateFormat().parse("15-12-2018 18:02:33")));
            listOfParkingXe.add(new Xe("12345678", null, null, "79N421138", MainProgram.getSimpleDateFormat().parse("13-12-2018 19:01:12")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        return duration * MainProgram.FEE_PER_HOUR;
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
