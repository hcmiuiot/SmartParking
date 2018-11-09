package main;

import org.opencv.core.Size;

public class Constants {

    public final static String  APPLICATION_TITLE = "SMART PARKING - HCM-IU - VNU";

    public final static String  LOGO_FILENAME = "hcmiulogo.png";

    public final static int     MAX_CAMERA_NUMBER = 10;

    public final static String  FXML_MAIN = "MainForm.fxml";
    public final static String  FXML_TRACKING = "TrackingForm.fxml";
    public final static String  FXML_TRACKING_CONFIG = "TrackingConfigForm.fxml";

    public static final Size    PREDICTION_CHAR_SIZE = new Size(10,10);
    public static final String  TRAIN_CHARS_DIR = ".data/demo/char";
    public static final String  TRAIN_OUTPUT_DIR = "./trainedData.txt";
    public static final Size    PREPROCESSING_RESIZE_SIZE = new Size(800, 600);
    public static final int     THRESHOLD_BLOCK_SIZE = 95;
    public static final int     THRESHOLD_C = 0;
    public static final int     MORPHOLOGY_SIZE = 2;
    public static final int     SZ = 20;
}
