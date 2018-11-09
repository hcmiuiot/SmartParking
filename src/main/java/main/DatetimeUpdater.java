package main;

import com.victorlaerte.asynctask.AsyncTask;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeUpdater extends AsyncTask {

    private Label updateLabel;
    public DatetimeUpdater(Label updateLabel) {
        this.updateLabel = updateLabel;
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public Object doInBackground(Object[] objects) {
        while (!isInterrupted()) {
            Platform.runLater(() -> {
                if (this.updateLabel != null)
                    this.updateLabel.setText(getCurrentTimeStamp());
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Object o) {

    }

    @Override
    public void progressCallback(Object[] objects) {

    }
}
