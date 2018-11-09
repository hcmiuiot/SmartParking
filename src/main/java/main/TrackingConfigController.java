package main;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class TrackingConfigController implements Initializable {

	private TrackingController trackingController = null;

	@FXML
	private JFXComboBox comboFontCamera;
	@FXML
	private JFXComboBox comboBehindCamera;
	
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
		int camCount = ImageProcessing.getNumOfAvailableCamera();
		for (int i=0; i < camCount; i++) {
			comboFontCamera.getItems().add("Camera " + i);
			comboBehindCamera.getItems().add("Camera " + i);
		}
//		comboFontCamera.getSelectionModel().select(0);
//		comboBehindCamera.getSelectionModel().select(0);
	}
	
}
