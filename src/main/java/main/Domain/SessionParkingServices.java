package main.Domain;

import main.Constants;

import java.util.ArrayList;

public class SessionParkingServices {
    private static SessionParkingServices instance;
    private static ArrayList<ParkingSession> listOfReservedParkingSession = new ArrayList<>();
    private static ArrayList<ParkingSession> listOfParkingParkingSession = new ArrayList<>();

    private SessionParkingServices() {
    }

    //Thread Safe getInstance
    public static SessionParkingServices getInstance() {
        if (instance == null) {
            synchronized (SessionParkingServices.class) {
                if (null == instance) {
                    instance = new SessionParkingServices();
                }
            }
        }
        return instance;
    }

    public Boolean checkExistRfidFromParkingList(String RFIDNumber) {
        for (int i = listOfParkingParkingSession.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that ParkingSession is parking
            if (listOfParkingParkingSession.get(i).getRfidNumber().toUpperCase().equals(RFIDNumber.toUpperCase()) && listOfParkingParkingSession.get(i).getStatus() == true)
                return true;
        }
        return false;
    }

    public ParkingSession getParkingSessionByRfidFromParkingList(String RFIDNumber) {
        for (int i = listOfParkingParkingSession.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that ParkingSession is parking
            if (listOfParkingParkingSession.get(i).getRfidNumber().toUpperCase().equals(RFIDNumber.toUpperCase()) && listOfParkingParkingSession.get(i).getStatus() == true)
                return listOfParkingParkingSession.get(i);
        }
        return null;
    }

    public long calculateParkingFee(Long duration) {
        return Math.max(duration * Constants.FEE_PER_HOUR, Constants.FEE_PER_HOUR);
    }

    public void moveParkingSessionToReservedList(ParkingSession parkingSession){
        for (int i = listOfParkingParkingSession.size() - 1; i >= 0; i--) {
            // If RFID is exist in DB and that ParkingSession is parking
            if (listOfParkingParkingSession.get(i).getRfidNumber().toUpperCase().equals(parkingSession.getRfidNumber().toUpperCase()) && listOfParkingParkingSession.get(i).getStatus() == true){
                listOfReservedParkingSession.add(listOfParkingParkingSession.get(i));
                listOfParkingParkingSession.remove(i);
                return;
            }
        }
    }

    public void addParkingSession(ParkingSession newParkingSession) {
        listOfParkingParkingSession.add(newParkingSession);
    }

}
