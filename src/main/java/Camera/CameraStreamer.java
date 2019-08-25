package Camera;

import com.victorlaerte.asynctask.AsyncTask;
import javafx.scene.image.ImageView;
import main.ImageProcessor.PlateNumberProcessing.ImageProcessing;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.TimeUnit;

public class CameraStreamer extends AsyncTask {

    private boolean isPlaying = false;
    private boolean isInitedVideoCapture = false;

    private VideoCapture vc;
    private int cameraIdx = 0;
    private Mat currentFrame;
    private int fps = 30;
    private long expectedSleeptime = 1000000l/fps;

    private ImageView imageViewer = null;

    public CameraStreamer(int cameraIdx) {
        this(cameraIdx, null);
    }

    public CameraStreamer(int cameraIdx, ImageView imageViewer) {
        this.vc = new VideoCapture(cameraIdx);
        this.cameraIdx = cameraIdx;
        this.imageViewer = imageViewer;
        if (vc != null)
            isInitedVideoCapture = true;
        setDaemon(true);
        backGroundThread.setDaemon(true);
    }

    public void stopStream() {
        this.isPlaying = false;
    }

    public void startStream() {
        this.isPlaying = true;
        this.execute();
    }

    public Mat getFrame() { return currentFrame; }

    public void setFps(int fps) {
        this.fps = fps;
        this.expectedSleeptime = 1000000l/fps;
    }

    @Override
    public void onPreExecute() {
        System.out.println("Stated thread!");
    }

    @Override
    public Object doInBackground(Object[] objects) {
        currentFrame = new Mat();

        long beginTime, endTime, delay;

        while (isPlaying) {
            beginTime = System.nanoTime();
            if ( !vc.isOpened()) {
                System.out.println("TRYING TO RECONNECT CAMERA #" + this.cameraIdx);
                vc.open(this.cameraIdx);
                System.out.println("CAMERA #" + this.cameraIdx + ":" + vc.isOpened());
            }
            vc.read(currentFrame);
            if (this.imageViewer != null) {
                ImageProcessing.setImage(this.imageViewer, this.currentFrame);
            }
            endTime = System.nanoTime();
            delay = (endTime - beginTime)/1000l;
            try {
                TimeUnit.MICROSECONDS.sleep(expectedSleeptime - delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Object o) {
        vc.release();
        System.out.println("Stopping thread!");
    }

    @Override
    public void progressCallback(Object[] objects) {


    }
}
