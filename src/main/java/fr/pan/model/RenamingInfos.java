package fr.pan.model;

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RenamingInfos {
	private Path oldPath;

	private String newFileName;
	
	private String extension;

}
