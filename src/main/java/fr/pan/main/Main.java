package fr.pan.main;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		LOGGER.info("PicturesAutoNamer is starting...");
		launch();
	}

	@Override
    public void start(Stage stage) throws IOException {
	    URL url = getClass().getResource("/layouts/main.fxml");
		VBox root = (VBox)FXMLLoader.load(url);
		Scene scene = new Scene(root,600,600);
		scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
    }
}

// a handy trick to launch JavaFX without using maven nor modules https://stackoverflow.com/questions/56551876/, that makes it easier to launch in debug
class JFXEasyLauncher {public static void main(String[] args) throws IOException, InterruptedException {Main.main(args);}}
