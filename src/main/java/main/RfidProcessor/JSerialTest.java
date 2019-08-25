package main.RfidProcessor;
// https://sourceforge.net/projects/javaarduinolibrary/files/

import com.fazecast.jSerialComm.SerialPort;

public class JSerialTest {
    private static JSerial js3 = new JSerial("COM10", 9600);

    public static void main(String[] agrs) {
        System.out.println(js3);
        SerialPort[] portNames = SerialPort.getCommPorts();
        for (int i = 0; i < portNames.length; i++)
            System.out.println(portNames[i].getSystemPortName());

        if (js3.openConnection()) {
            System.out.println("Open Successful");
            while (true) {
                String s = js3.serialRead();
                if (s.length() > 0)
                    System.out.println(s);
            }
        } else {
            System.out.println("Open Failed");
        }
    }
}
