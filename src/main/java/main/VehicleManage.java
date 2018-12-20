package main;

import java.util.ArrayList;

public class VehicleManage {
    private static VehicleManage instance;
    private static ArrayList<Vehicle> listOfOtherVehicle = new ArrayList<>();
    private static ArrayList<Vehicle> listOfParkingVehicle = new ArrayList<>();

    private VehicleManage() {
    }

    //Thread Safe getInstance
    public static VehicleManage getInstance() {
        if (instance == null) {
            synchronized (VehicleManage.class) {
                if (null == instance) {
                    instance = new VehicleManage();
                }
            }
        }
        return instance;
    }

    public static Boolean isExistRfidFromParkingList(String RFIDNumber) {
        for (int i = listOfParkingVehicle.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that Vehicle is parking
            if (listOfParkingVehicle.get(i).getRfidNumber().toUpperCase().equals(RFIDNumber.toUpperCase()) && listOfParkingVehicle.get(i).getStatus() == true)
                return true;
        }
        return false;
    }

    public static Vehicle getXeByRfidFromParkingList(String RFIDNumber) {
        for (int i = listOfParkingVehicle.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that Vehicle is parking
            if (listOfParkingVehicle.get(i).getRfidNumber().toUpperCase().equals(RFIDNumber.toUpperCase()) && listOfParkingVehicle.get(i).getStatus() == true)
                return listOfParkingVehicle.get(i);
        }
        return null;
    }

    public static long calculateParkingFee(Long duration) {
        return duration * Constants.FEE_PER_HOUR;
    }

    public void moveXeToOtherList(Vehicle vehicle){
        for (int i = listOfParkingVehicle.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that Vehicle is parking
            if (listOfParkingVehicle.get(i).getRfidNumber().toUpperCase().equals(vehicle.getRfidNumber().toUpperCase()) && listOfParkingVehicle.get(i).getStatus() == true){
                listOfOtherVehicle.add(listOfParkingVehicle.get(i));
                listOfParkingVehicle.remove(i);
                return;
            }
        }
    }

    public static void addXe(Vehicle newVehicle) {
        listOfParkingVehicle.add(newVehicle);
    }

}
