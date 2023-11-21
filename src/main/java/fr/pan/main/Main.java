package fr.pan.main;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	
    private static final LogManager logManager = LogManager.getLogManager();

    private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );

    static{

    }

	public static void main(String[] args) throws IOException, InterruptedException {
		launch();

		/*
		//Launching the bat



	    
*/

	}
	/*
	private static void launchQuery() throws IOException, InterruptedException {
		String prompt = "A short but descriptive 5-10 words filename in camelCase with no whitespace for this image could be:";
		System.out.println(prompt);
		
		ObjectMapper objectMapper = new ObjectMapper();
		ImageData imageData = ImageData.builder()
								.data(base64Img)
								.id(1)
								.build();
		Body body = Body.builder()
					.prompt(prompt)
					.temperature(0.1)
					.imageData(List.of(imageData))
					.build();

		//quick test
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(URI.create("http://127.0.0.1:8080/completion"))
	          .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
	          .build();

	    HttpResponse<String> response =
	          client.send(request, BodyHandlers.ofString());

	    System.out.println(response.body());
	    
		// launch llama.cpp llava server
		//server -m E:\crea\txt\gguf\ggml-model-q5_k.gguf --mmproj E:\crea\txt\gguf\llava-13B-mmproj-model-f16.gguf -c 2048 --port 8080 -ngl 35 -mg 3 -t 20

		
		//open the directory E:\imgtestprjava
		
		//
	}
	*/
	@Override
    public void start(Stage stage) throws IOException {
	    System.out.println("classpath=" + System.getProperty("java.class.path"));
	    URL url = getClass().getResource("/layouts/main.fxml");
		VBox root = (VBox)FXMLLoader.load(url);
		Scene scene = new Scene(root,600,600);
		scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		
        try {
            logManager.readConfiguration( getClass().getResource("/layouts/logging.properties").openStream() );
            LOGGER.log(Level.INFO, "PicturesAutoNamer is starting...");
        } catch ( IOException exception ) {
            LOGGER.log( Level.SEVERE, "Cannot read configuration file", exception );
        }
    }
}
