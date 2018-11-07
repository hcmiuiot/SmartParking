package Camera;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class StreamingThread implements Runnable {

    private boolean isPlaying = false;
    private VideoCapture vc;

    public StreamingThread(int cameraIdx) {
        vc = new VideoCapture(cameraIdx);
    }

    @Override
    public void run() {
        isPlaying = true;
        Mat frame = new Mat();
        while (isPlaying) {

        }
    }
}
