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
import javafx.scene.layout.AnchorPane;

public class MainController implements Initializable {
	@FXML
	private Label lblTime;
	@FXML
	private SplitPane splitPane;
	
	Parent trackingForm1, trackingForm2;
	
	public void initialize(URL location, ResourceBundle resources) {
		ImageProcessing.getInstance();
		
		FXMLLoader loader1 = new FXMLLoader(MainProgram.class.getResource("/TrackingForm.fxml"));
		FXMLLoader loader2 = new FXMLLoader(MainProgram.class.getResource("/TrackingForm.fxml"));
		
		try {	
			trackingForm1 = loader1.load();
			TrackingController trackingController1 = loader1.getController();
			trackingForm2 = loader2.load();
			TrackingController trackingController2 = loader2.getController();	
		} catch (IOException e) {
			e.printStackTrace();
		}

		splitPane.getItems().add(trackingForm1);
		splitPane.getItems().add(trackingForm2);
	}

}
