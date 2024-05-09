package fr.pan.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.model.RenamingInfos;
import fr.pan.server.ServerQuerier;

public class FilesRenamerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	
	
	FilesRenamer filesRenamer = new FilesRenamer();

	@Test
	public void shouldRenameSpacesInCamelCase() {
		// GIVEN
		List<RenamingInfos> renamingInfosList = new ArrayList<>();
		renamingInfosList.add(new RenamingInfos(Path.of("oldImageName"), "a red dog", ""));
		
		// WHEN
		this.filesRenamer.cleanFileNames(renamingInfosList);
		
		// THEN
		assertEquals("ARedDog", renamingInfosList.get(0).getNewFileName());
	}

	@Test
	public void shouldRenameSnakeCaseInCamelCase() {
		// GIVEN
		List<RenamingInfos> renamingInfosList = new ArrayList<>();
		renamingInfosList.add(new RenamingInfos(Path.of("oldImageName"), "a_red_dog", ""));
		
		// WHEN
		this.filesRenamer.cleanFileNames(renamingInfosList);
		
		// THEN
		assertEquals("ARedDog", renamingInfosList.get(0).getNewFileName());
	}	

	@Test
	public void shouldIncrementIfNameCollideWithAFileInTheFolder(@TempDir Path tempDir) throws IOException {
		// GIVEN
		Path newFile = Files.createFile(tempDir.resolve("ARedDog.png"));
		System.out.println(Files.exists(newFile));
		
		// WHEN
		String retour = this.filesRenamer.getNewNameForCollidingWithExistingFile(new RenamingInfos(tempDir.resolve("oldImageName"), "ARedDog", ".png"), newFile);
		
		// THEN
		assertEquals("ARedDog_2.png", retour);

	}
	
	@Test
	public void shouldIncrementIfNameCollideWithAFileAlreadyIncrementedInTheFolder(@TempDir Path tempDir) throws IOException {
		// GIVEN
		Path newFile = Files.createFile(tempDir.resolve("ARedDog_44.png"));
		System.out.println(Files.exists(newFile));
		
		// WHEN
		String retour = this.filesRenamer.getNewNameForCollidingWithExistingFile(new RenamingInfos(tempDir.resolve("oldImageName"), "ARedDog", ".png"), newFile);
		
		// THEN
		assertEquals("ARedDog_45.png", retour);

	}

	@Test
	public void shouldIncrementIfNameCollideWithAFileAlreadyIncrementedInTheFolderWithoutExtension(@TempDir Path tempDir) throws IOException {
		// GIVEN
		Path newFile = Files.createFile(tempDir.resolve("ARedDog_44"));
		System.out.println(Files.exists(newFile));
		
		// WHEN
		String retour = this.filesRenamer.getNewNameForCollidingWithExistingFile(new RenamingInfos(tempDir.resolve("oldImageName"), "ARedDog", ""), newFile);
		
		// THEN
		assertEquals("ARedDog_45", retour);

	}

}
