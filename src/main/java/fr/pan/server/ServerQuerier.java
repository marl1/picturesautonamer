package fr.pan.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pan.model.Body;
import fr.pan.model.ImageData;

public class ServerQuerier {
	private static final Logger LOGGER = Logger.getLogger(ServerQuerier.class.getPackage().getName());
	
	public static void launchQuery(String base64Img, String prompt)  {
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
	    HttpRequest request;
		try {
			request = HttpRequest.newBuilder()
			      .uri(URI.create("http://127.0.0.1:8080/completion"))
			      .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
			      .build();
			try {
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				System.out.println(response.body());
			} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
			}
			
		} catch (JsonProcessingException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
		}

	    


	    
		// launch llama.cpp llava server
		//server -m E:\crea\txt\gguf\ggml-model-q5_k.gguf --mmproj E:\crea\txt\gguf\llava-13B-mmproj-model-f16.gguf -c 2048 --port 8080 -ngl 35 -mg 3 -t 20

		
		//open the directory E:\imgtestprjava
		
		//
	}
}
