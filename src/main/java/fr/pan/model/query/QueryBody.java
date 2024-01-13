package fr.pan.model.query;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record QueryBody(String prompt, Double temperature, Double top_k, Double top_p, List<ImageData> imageData) {
	
	@JsonProperty("image_data")
	public List<ImageData> getImageData() {
	    return imageData;
	}
}
