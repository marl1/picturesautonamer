package fr.pan.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.constant.ImageFileTypes;
import fr.pan.model.RenamingInfos;
import fr.pan.server.ServerQuerier;

public class FilesRenamer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	

	public void rename(List<RenamingInfos> renamingInfosList) {
		//TODO a method to check if an oldname=newname in renamingInfosList
		
		
		for(RenamingInfos renamingInfos:clean(renamingInfosList)) {
			try {
				Files.move(renamingInfos.getOldPath(), renamingInfos.getOldPath().getParent().resolve(renamingInfos.getNewFileName()));
			} catch (Exception e) {
				LOGGER.error("Couldn't rename {}", renamingInfos.getOldPath().toString());
			}
		}
		
	}
	
	public List<RenamingInfos> clean(List<RenamingInfos> renamingInfosList) {
		
		List<String> oldFileNameList = renamingInfosList.stream().map(renamingInfos -> renamingInfos.getOldPath().getFileName().toString()).toList();
		List<String> newFileNameList = renamingInfosList.stream().map(renamingInfos -> renamingInfos.getNewFileName()).toList();
		Map<String, Integer> namesOccurencesMap = new HashMap<>();
		
		for(RenamingInfos renamingInfos:renamingInfosList) {
			if (renamingInfosList == null) {
				continue;
			}
			
			//cleaning whitespaces at the start and the end 
			renamingInfos.setNewFileName(renamingInfos.getNewFileName().trim());

			//Removing forbidden filename char
			renamingInfos.setNewFileName(renamingInfos.getNewFileName().replaceAll("[\\\\/:*?\"<>|]", ""));
			
			renamingInfos.setNewFileName(renamingInfos.getNewFileName().substring(0, 1).toUpperCase() + renamingInfos.getNewFileName().substring(1));
			
			//Force it in CamelCase.
			if (renamingInfos.getNewFileName().contains("_")) {
				renamingInfos.setNewFileName(CaseUtils.toCamelCase(renamingInfos.getNewFileName(), true, new char[]{'_'}));
			}
			if (renamingInfos.getNewFileName().contains("-")) {
				renamingInfos.setNewFileName(CaseUtils.toCamelCase(renamingInfos.getNewFileName(), true, new char[]{'-'}));
			}

			if (renamingInfos.getNewFileName().contains(" ")) {
				renamingInfos.setNewFileName(CaseUtils.toCamelCase(renamingInfos.getNewFileName(), true, new char[]{' '}));
			}
			
			//The LLM gave an extension? We remove it.
			if(ImageFileTypes.COMMON_TYPES.stream().anyMatch(type -> renamingInfos.getNewFileName().endsWith(type))) {
				renamingInfos.setNewFileName(renamingInfos.getNewFileName().substring(0,renamingInfos.getNewFileName().length()-4));
				
			}

			//The LLM added a . at the end? We remove it.
			if (renamingInfos.getNewFileName().endsWith(".")) {
				renamingInfos.setNewFileName(renamingInfos.getNewFileName().substring(0, renamingInfos.getNewFileName().length()-1));
			}
			
			incrementOnIdenticalFileName(renamingInfosList, renamingInfos);

			//We re-add the original extension, if there was one
			String oldFileName = renamingInfos.getOldPath().getFileName().toString();
			if (oldFileName.contains(".")) {
				renamingInfos.setNewFileName(renamingInfos.getNewFileName() + oldFileName.substring(oldFileName.lastIndexOf(".")));
			}
			LOGGER.info("{} will be renamed {}.",renamingInfos.getOldPath(), renamingInfos.getNewFileName());
		}
		return renamingInfosList;
	}

	public void incrementOnIdenticalFileName(List<RenamingInfos> renamingInfosList, RenamingInfos renamingInfos) {
		//How many of files that have the same name? (excluding numbers at the end)
		List<String> filesWithSameNameIgnoringTrailingNumbers =
				renamingInfosList.stream()
				.filter(fileInfo -> fileInfo.getNewFileName().replaceAll("[\\d_]*$", "") // regex to check if ARedDog_1 match ARedDog or ARedDog_56
									.equals(renamingInfos.getNewFileName().replaceAll("[\\d_]*$", "")))
				.map(fileInfo -> fileInfo.getNewFileName())
				.collect(Collectors.toList());
		if(filesWithSameNameIgnoringTrailingNumbers.size()>1) {
			// there is more than 1, meaning the current file and another one
			Collections.sort(filesWithSameNameIgnoringTrailingNumbers);
			Collections.reverse(filesWithSameNameIgnoringTrailingNumbers); // let sort them in reverse to put the biggest number on top
			String biggestFileName = filesWithSameNameIgnoringTrailingNumbers.get(0);
			int lastKnownNumber = 0;
			if (Character.isDigit(biggestFileName.charAt(biggestFileName.length()-1))) {
				// we get the digits at the end
				lastKnownNumber = Integer.parseInt(filesWithSameNameIgnoringTrailingNumbers.get(0).replaceFirst("^.*\\D",""));
			}
			renamingInfos.setNewFileName(renamingInfos.getNewFileName()+"_"+(lastKnownNumber+1)); // we add the number at the end of the file name
		}
	}

	public static List<RenamingInfos> changeNameIfAlreadyExists(List<RenamingInfos> renamingInfosList) {
		return renamingInfosList;
	}
}
