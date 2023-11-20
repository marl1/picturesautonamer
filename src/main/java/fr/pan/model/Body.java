package fr.pan.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record Body(String prompt, Double temperature, List<ImageData> imageData) {
	
	@JsonProperty("image_data")
	public List<ImageData> getImageData() {
	    return imageData;
	}
}
