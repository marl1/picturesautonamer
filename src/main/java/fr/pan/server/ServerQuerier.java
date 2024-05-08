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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pan.model.query.QueryBody;
import fr.pan.model.response.ResponseBody;
import fr.pan.model.query.ImageData;

public class ServerQuerier {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	

	public static String launchQuery(String base64Img, String prompt)  {
		String stringToReturn = "";
		LOGGER.info("---------NEW QUERY-----------");
		LOGGER.info(prompt);
		LOGGER.info("-----------------------------");
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ImageData imageData = ImageData.builder()
								.data(base64Img)
								.id(1)
								.build();
		QueryBody body = QueryBody.builder()
					.prompt(prompt)
					.temperature(0.01)
					.top_k(1.0)
					.top_p(0.01)
					.imageData(List.of(imageData))
					.build();
		

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request;
		try {
			String bodyInString = objectMapper.writeValueAsString(body);
			LOGGER.info(bodyInString);
			request = HttpRequest.newBuilder()
			      .uri(URI.create("http://127.0.0.1:8080/completion"))
			      .POST(HttpRequest.BodyPublishers.ofString(bodyInString))
			      .build();
			try {
				HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
				ResponseBody r = objectMapper.readValue(response.body(), ResponseBody.class);	
				LOGGER.info(response.body());
				stringToReturn = r.content();
			} catch (IOException | InterruptedException e) {
				LOGGER.error(e.getLocalizedMessage());
			}
			
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getLocalizedMessage());
		}
		return stringToReturn;
	}
}
