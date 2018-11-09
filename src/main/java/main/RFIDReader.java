package main;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

public class RFIDReader {

    public static void main(String[] args) {
        HidServices hidServices = HidManager.getHidServices();
        HidDevice rfid = null;
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            System.out.println(hidDevice.getManufacturer() + "|" + hidDevice.getVendorId() + "|" + hidDevice.getProductId());
            String t = hidDevice.getManufacturer();
            if (t != null && t.toLowerCase().contains("rfid"))
                rfid = hidDevice;
        }
        rfid.open();
        while (true) {
            byte[] data = new byte[1024];
            rfid.read(data);
            System.out.println(new String(data));
        }
        //hidServices.addHidServicesListener(this);
//        hidServices.getHidDevice(2303, 9, "*");
        //System.out.println(rfid.getManufacturer());
    }

}
