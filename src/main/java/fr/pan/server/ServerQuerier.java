package fr.pan.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pan.model.ImageData;
import fr.pan.model.query.Body;

public class ServerQuerier {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	

	public static void launchQuery(String base64Img, String prompt)  {
		LOGGER.info("---------NEW QUERY-----------");
		LOGGER.info(prompt);
		LOGGER.info("-----------------------------");
		
		ObjectMapper objectMapper = new ObjectMapper();
		ImageData imageData = ImageData.builder()
								.data(base64Img)
								.id(1)
								.build();
		Body body = Body.builder()
					.prompt(prompt)
					.temperature(1.0)
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
				LOGGER.info(response.body());
			} catch (IOException | InterruptedException e) {
				LOGGER.error(e.getLocalizedMessage());
			}
			
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getLocalizedMessage());
		}

	}
}
