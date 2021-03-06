package main.JavaFxGui.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.ImageProcessor.PlateNumberProcessing.ImageProcessing;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackingConfigController implements Initializable {

	private TrackingController trackingController = null;

	@FXML
	private JFXComboBox comboFrontCamera;
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
	@FXML
	private JFXComboBox laneRole_combobox;

	public void setTrackingController(TrackingController trackingController) {
		this.trackingController = trackingController;
	}
	
	@FXML
	void onCancel(ActionEvent event) {
		Stage stage = (Stage) laneRole_combobox.getScene().getWindow();
		stage.close();
	}

	@FXML
		void onOkay(ActionEvent event) {
		switch (laneRole_combobox.getValue().toString()) {
			case "Enter and Out":
				trackingController.setRole((byte) 0);
				break;
			case "Only Enter":
				trackingController.setRole((byte) 1);
				break;
			case "Only Out":
				trackingController.setRole((byte) 2);
				break;
		}
		trackingController.setCamSource(comboFrontCamera.getSelectionModel().getSelectedIndex(),
				comboBehindCamera.getSelectionModel().getSelectedIndex());
		trackingController.onStartCam(null);
		Stage stage = (Stage) laneRole_combobox.getScene().getWindow();
		stage.close();
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
			comboFrontCamera.getItems().add(new Label("Camera " + i));
			comboBehindCamera.getItems().add("Camera " + i);
		}
//		comboFrontCamera.getSelectionModel().select(0);
//		comboBehindCamera.getSelectionModel().select(0);
		laneRole_combobox.getItems().add("Enter and Out");
		laneRole_combobox.getItems().add("Only Enter");
		laneRole_combobox.getItems().add("Only Out");
		laneRole_combobox.getSelectionModel().selectFirst();

	}
	
}
