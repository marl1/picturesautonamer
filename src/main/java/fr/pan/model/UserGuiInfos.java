package fr.pan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGuiInfos {
	private final String folderToAnalyze;

	private final String prompt;
	
	private final boolean includingSubDirectories;

}
