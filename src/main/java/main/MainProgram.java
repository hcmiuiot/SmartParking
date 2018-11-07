package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.applet.Main;

public class MainProgram extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.getIcons().add(new Image("hcmiulogo.png"));
		FXMLLoader loader = new FXMLLoader(MainProgram.class.getResource("/MainForm.fxml"));
		Parent mainform = loader.load();
		Scene mainScence = new Scene(mainform);
		primaryStage.setScene(mainScence);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("SMART PARKING - HCM-IU - VNU");
		
//		Database.getInstance(); //Connect 2 DB
		primaryStage.show();


	}

	
}
