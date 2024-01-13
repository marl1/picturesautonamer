package fr.pan.model;

import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RenamingInfos {
	private Path oldPath;

	private String newFileName;

}
