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
		
		renamingInfosList = cleanFileNames(renamingInfosList);
		for(RenamingInfos renamingInfos:renamingInfosList) {
			Path newPath = renamingInfos.getOldPath().getParent().resolve(renamingInfos.getNewFileName() + renamingInfos.getExtension());
			
			if(Files.exists(newPath)) { // we want to rename the file but a file with the same name already exist in the folder
				newPath = renamingInfos.getOldPath().getParent().resolve(getNewNameForCollidingWithExistingFile(renamingInfos, newPath));
			}
			try {
				Files.move(renamingInfos.getOldPath(), newPath);
			} catch (Exception e) {
				LOGGER.error("Couldn't rename {}", renamingInfos.getOldPath().toString());
			}
		}
		
	}

	/**
	 * In case the new name collide with a file already existing in the folder
	 * @param renamingInfos
	 * @param newPath
	 * @return the new file name
	 */
	public String getNewNameForCollidingWithExistingFile(RenamingInfos renamingInfos, Path newPath) {
		LOGGER.info("We want to create {} but it already exists in the folder.", newPath);
		int lastKnownNumber = 1;
		while(Files.exists(newPath)) {
			if (Character.isDigit(newPath.toString()
					.charAt(newPath.toString().length()-(1+renamingInfos.getExtension().length())))) {
				// we use the digits at the end to increment
				lastKnownNumber = Integer.parseInt(newPath.toString().substring(0, newPath.toString().length()-(renamingInfos.getExtension().length())).replaceFirst("^.*\\D",""));
			}
			renamingInfos.setNewFileName(renamingInfos.getNewFileName().replaceAll("[\\d_]*$", "")+"_"+(lastKnownNumber+1));
			newPath = renamingInfos.getOldPath().getParent().resolve(renamingInfos.getNewFileName() + renamingInfos.getExtension());
		}
		LOGGER.info("It will become {}.", newPath.getFileName());
		return newPath.getFileName().toString();
	}
	
	public List<RenamingInfos> cleanFileNames(List<RenamingInfos> renamingInfosList) {

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

			LOGGER.info("\"{}\" will be renamed {}.",renamingInfos.getOldPath(), renamingInfos.getNewFileName()+renamingInfos.getExtension());
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
				lastKnownNumber = Integer.parseInt(biggestFileName.replaceFirst("^.*\\D",""));
			}
			renamingInfos.setNewFileName(renamingInfos.getNewFileName()+"_"+(lastKnownNumber+1)); // we add the number at the end of the file name
		}
	}

	public static List<RenamingInfos> changeNameIfAlreadyExists(List<RenamingInfos> renamingInfosList) {
		return renamingInfosList;
	}
}
