package fr.pan.model;

import fr.pan.controller.MainController;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGuiInfos {
	private final MainController mainController;

	private final String folderToAnalyze;

	private final String prompt;
	
	private final boolean includingSubDirectories;

}
