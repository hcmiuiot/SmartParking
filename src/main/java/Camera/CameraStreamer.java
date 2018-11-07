package Camera;

import javafx.scene.image.Image;
import org.opencv.core.Mat;

public class CameraStreamer {

    public CameraStreamer(int cameraIdx) {

    }

    public int getFps() {
        return 0;
    }

    public boolean startStream() {
        return true;
    }

    public Mat grabMat() {
        return new Mat();
    }

    public Image grabImage() {
        return null;
    }

    public boolean stopStream() {
        return true;
    }

}
