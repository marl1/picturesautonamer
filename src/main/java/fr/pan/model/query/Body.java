package fr.pan.model.query;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.pan.model.ImageData;
import lombok.Builder;

@Builder
public record Body(String prompt, Double temperature, List<ImageData> imageData) {
	
	@JsonProperty("image_data")
	public List<ImageData> getImageData() {
	    return imageData;
	}
}
