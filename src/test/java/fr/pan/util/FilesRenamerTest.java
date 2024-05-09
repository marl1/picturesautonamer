package fr.pan.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.model.RenamingInfos;
import fr.pan.server.ServerQuerier;

public class FilesRenamerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	
	
	FilesRenamer filesRenamer = new FilesRenamer();

	@Test
	public void shouldIncrementIfNameAlreadyInTheNewNamesList() {
		// GIVEN
		List<RenamingInfos> renamingInfosList = new ArrayList<>();
		//an image representing a red dog already existed
		renamingInfosList.add(new RenamingInfos(Path.of("oldImageName"), "ARedDog"));

		//...an we have another image of the same red dog, thus, name collision
		renamingInfosList.add(new RenamingInfos(Path.of("anotherOldImageName"), "ARedDog"));

		
		// WHEN
		this.filesRenamer.incrementOnIdenticalFileName(renamingInfosList, renamingInfosList.get(1));
		
		// THEN
		assertEquals("ARedDog_1", renamingInfosList.get(1).getNewFileName());
	}


	@Test
	public void shouldIncrementIfNameAlreadyHasBeenIncrementedInTheNewNamesList() {
		// GIVEN
		List<RenamingInfos> renamingInfosList = new ArrayList<>();
		//an image representing a red dog already existed
		renamingInfosList.add(new RenamingInfos(Path.of("oldImageName"), "ARedDog"));

		//...an we have another image of the same red dog
		renamingInfosList.add(new RenamingInfos(Path.of("anotherOldImageName"), "ARedDog_1"));
		
		//...an we have ANOTHER another image of the same red dog
		renamingInfosList.add(new RenamingInfos(Path.of("anotherOldImageName"), "ARedDog_2"));
		

		//...an we have ANOTHER ANOTHER another image of the same red dog, thus, name collision
		renamingInfosList.add(new RenamingInfos(Path.of("anotherOldImageName"), "ARedDog"));
		
		RenamingInfos fileUnderTest = renamingInfosList.get(3);

		
		// WHEN
		this.filesRenamer.incrementOnIdenticalFileName(renamingInfosList, fileUnderTest);
		
		// THEN
		System.out.println(renamingInfosList);
		assertEquals("ARedDog_3", fileUnderTest.getNewFileName());
	}

}
