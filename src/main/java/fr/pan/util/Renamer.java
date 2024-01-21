package fr.pan.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.constant.ImageFileTypes;
import fr.pan.model.RenamingInfos;
import fr.pan.server.ServerQuerier;

public class Renamer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	

	public static void rename(List<RenamingInfos> renamingInfosList) {
		//TODO a method to check if an oldname=newname in renamingInfosList
		
		
		for(RenamingInfos renamingInfos:clean(renamingInfosList)) {
			try {
				Files.move(renamingInfos.getOldPath(), renamingInfos.getOldPath().getParent().resolve(renamingInfos.getNewFileName()));
			} catch (Exception e) {
				LOGGER.error("Couldn't rename {}", renamingInfos.getOldPath().toString());
			}
		}
		
	}
	
	public static List<RenamingInfos> clean(List<RenamingInfos> renamingInfosList) {
		
		List<String> oldFileNameList = renamingInfosList.stream().map(renamingInfos -> renamingInfos.getOldPath().getFileName().toString()).toList();
		List<String> newFileNameList = renamingInfosList.stream().map(renamingInfos -> renamingInfos.getNewFileName()).toList();
		Map<String, Integer> namesOccurencesMap = new HashMap<>();
		
		for(RenamingInfos renamingInfos:renamingInfosList) {
			String originalNewName = renamingInfos.getNewFileName();
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

			//The LLM forgot a . at the end?
			if (renamingInfos.getNewFileName().endsWith(".")) {
				renamingInfos.setNewFileName(renamingInfos.getNewFileName().substring(0, renamingInfos.getNewFileName().length()-1));
			}
			
			//Another file(s) with the same new name? We add the number index at the end.
			if (newFileNameList.stream().filter(n -> n.equals(originalNewName)).count() > 1) {
				namesOccurencesMap.putIfAbsent(renamingInfos.getNewFileName(), 1);
				Integer num = namesOccurencesMap.replace(renamingInfos.getNewFileName(), namesOccurencesMap.get(renamingInfos.getNewFileName())+1);
				if(num>1)
					renamingInfos.setNewFileName(renamingInfos.getNewFileName()+num);
			}
			
			//We readd the original extension
			String oldFileName = renamingInfos.getOldPath().getFileName().toString();
			renamingInfos.setNewFileName(renamingInfos.getNewFileName() + oldFileName.substring(oldFileName.lastIndexOf("."))); 
			LOGGER.info("{} will be renamed {}.",renamingInfos.getOldPath(), renamingInfos.getNewFileName());
		}
		return renamingInfosList;
	}
	
	public static List<RenamingInfos> changeNameIfAlreadyExists(List<RenamingInfos> renamingInfosList) {


		
		
		return renamingInfosList;
	}
}
