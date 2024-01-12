package fr.pan.model;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerLaunchInfos {
	private final String folderToAnalyze;

	private final String prompt;

}