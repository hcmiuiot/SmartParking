package main.RfidProcessor;

public class RFIDHandler {
    private static RFIDHandler instance;


    private RFIDHandler() {

    }

    public static RFIDHandler getInstance() {
        if (instance == null) {
            synchronized (RFIDHandler.class) {
                if (null == instance) {
                    instance = new RFIDHandler();
                }
            }
        }
        return instance;
    }

    public static Boolean checkValidRFID(String RFIDNumber) {
        if (RFIDNumber.length() < 8) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            if (RFIDNumber.toUpperCase().charAt(i) > 'F')
                return false;
        }
        return true;
    }
}
