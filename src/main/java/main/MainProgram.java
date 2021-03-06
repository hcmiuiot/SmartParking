package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.ImageProcessor.EmotionalProcessing.EmotionDetector;

import java.text.SimpleDateFormat;

public class MainProgram extends Application {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("-------INITIALIZATION-------");

        primaryStage.getIcons().add(new Image(Constants.LOGO_FILENAME));
        FXMLLoader loader = new FXMLLoader(MainProgram.class.getResource("/" + Constants.FXML_MAIN));
        Parent mainform = loader.load();
        Scene mainScence = new Scene(mainform);
        primaryStage.setScene(mainScence);
        primaryStage.setMaximized(true);
        primaryStage.setTitle(Constants.APPLICATION_TITLE);

        // Init the EmotionDetector singleton
        try {
            EmotionDetector.getInstance();
            System.out.println("Emotional initialized!");
        } catch (Exception e){
            System.err.println("Error in EmotionDetector, please check credential or the internet connection! Please note that there is no Bug :( - XT");
        }

        //		Database.getInstance(); //Connect 2 DB
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("PROGRAM EXITING");
            Platform.exit();
            System.exit(0);
        });
        System.out.println("-------END_OF_INIT-------");
        primaryStage.show();
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return sdf;
    }


}
