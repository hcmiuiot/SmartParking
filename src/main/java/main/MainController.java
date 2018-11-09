package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;

public class MainController implements Initializable {
	@FXML
	private Label lblTime;
	@FXML
	private SplitPane splitPane;
	
	Parent trackingForm1, trackingForm2;

	
	public void initialize(URL location, ResourceBundle resources) {
		ImageProcessing.getInstance();

		DatetimeUpdater watcher = new DatetimeUpdater(lblTime);
		watcher.setDaemon(true);
		watcher.execute();
		
		FXMLLoader loader1 = new FXMLLoader(MainProgram.class.getResource("/" + Constants.FXML_TRACKING));
		FXMLLoader loader2 = new FXMLLoader(MainProgram.class.getResource("/" + Constants.FXML_TRACKING));
		
		try {	
			trackingForm1 = loader1.load();
			TrackingController trackingController1 = loader1.getController();
			trackingForm2 = loader2.load();
			TrackingController trackingController2 = loader2.getController();	
		} catch (IOException e) {
			e.printStackTrace();
		}

		splitPane.setDividerPosition(0, splitPane.getWidth()/2);
		splitPane.getItems().add(trackingForm1);
		splitPane.getItems().add(trackingForm2);
	}

}
