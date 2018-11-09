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

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainProgram extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.getIcons().add(new Image(Constants.LOGO_FILENAME));
		FXMLLoader loader = new FXMLLoader(MainProgram.class.getResource("/" + Constants.FXML_MAIN));
		Parent mainform = loader.load();
		Scene mainScence = new Scene(mainform);
		primaryStage.setScene(mainScence);
		primaryStage.setMaximized(true);
		primaryStage.setTitle(Constants.APPLICATION_TITLE);

		Database.getInstance(); //Connect 2 DB
		primaryStage.setOnCloseRequest(event -> {
			System.out.println("Reach main here");
			Platform.exit();
			System.exit(0);
		});
		primaryStage.show();
	}

	
}
