package fr.pan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerLaunchInfos {
	private final String folderToAnalyze;

	private final String prompt;

}
