package main;

import java.net.URL;
import java.security.cert.TrustAnchor;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

public class TrackingConfigController implements Initializable {

	private TrackingController trackingController = null;
	
	@FXML
	private JFXComboBox comboCamera;
	
	@FXML
    private JFXSlider focusWidth;

    @FXML
    private JFXSlider focusHeight;

    @FXML
    private JFXSlider focusX;

    @FXML
    private JFXSlider focusY;
	
	public void setTrackingController(TrackingController trackingController) {
		this.trackingController = trackingController;
	}
	
	@FXML
    void onCancel(ActionEvent event) {

    }

    @FXML
    void onOkay(ActionEvent event) {
    	
    }
    
    @FXML
    void onChangeFocusConfigs(MouseEvent event) {
    	if (trackingController != null) {
    		trackingController.setFocusConfig((int)focusWidth.getValue(), (int)focusHeight.getValue(), (int)focusX.getValue(), (int)focusY.getValue());
    	}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		comboCamera.getItems().add("Camera 0");
		comboCamera.getItems().add("Camera 1");
		comboCamera.getItems().add("Camera 2");
		comboCamera.getItems().add("Camera 3");
		comboCamera.getSelectionModel().select(trackingController.getDeviceIndex());
	}
	
}
