package fr.pan.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.model.RenamingInfos;
import fr.pan.model.UserGuiInfos;
import fr.pan.server.ServerLauncher;
import fr.pan.server.ServerQuerier;

public class FilesProcesser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesProcesser.class);

	public void startFileProcessing(UserGuiInfos userGuiInfos) {
    	List<Path> fileList = listFiles(userGuiInfos);
    	LOGGER.info("There is {} image(s) to process.", fileList.size());
    	processFiles(userGuiInfos, fileList);
	}
	
	private void processFiles(UserGuiInfos userGuiInfos, List<Path> fileList) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		List<RenamingInfos> renamingInfosList = new ArrayList<>();
		 for(Path p: fileList) {
			 LOGGER.info("Processing \"{}\"...", p);
			 
			 Optional<String> imgBase64 = getImgInBase64(p);
			 if(imgBase64.isPresent()) {
				 String newName = ServerQuerier.launchQuery(imgBase64.get(), userGuiInfos.getPrompt());
				renamingInfosList.add(new RenamingInfos(p, newName, getFileExtension(p)));
		 	 }

		 }
		 service.submit(() -> new FilesRenamer().rename(renamingInfosList));
		 service.submit(() -> ServerLauncher.destroyServerProcess());
			
	}

	private String getFileExtension(Path p) {
		String toReturn = "";
		if (p.getFileName().toString().contains(".")) {
			toReturn = p.getFileName().toString().substring(p.getFileName().toString().lastIndexOf("."));
		}
		LOGGER.info("The file extension is " + toReturn);
		return toReturn;
	}
	
	private List<Path> listFiles(UserGuiInfos userGuiInfos) {
		List<Path> listToReturn = new ArrayList<>();
		int folderDepth = 1;
		if (userGuiInfos.isIncludingSubDirectories()) {
			folderDepth = Integer.MAX_VALUE; // won't work if there is more than 2,147,483,647 nested directories, oh no.
		}
	    try (Stream<Path> stream = Files.walk(Paths.get(userGuiInfos.getFolderToAnalyze()), folderDepth)) {
	    	listToReturn.addAll(stream
	        		 				.filter(file -> !Files.isDirectory(file))
	        		 				.toList());
	    } catch (IOException e) {
			LOGGER.error("FAILED to read the folder \"{}\"", userGuiInfos.getFolderToAnalyze());
		}
	    return listToReturn;
	}
	
	private Optional<String> getImgInBase64(Path path) {
		String toReturn;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
			LOGGER.info("Generating thumbnail for {}...", path);
			BufferedImage originalImage = ImageIO.read(path.toFile());
		    BufferedImage resizedImage = Scalr.resize(originalImage, 10);

		    LOGGER.info("Converting...");
		    final BufferedImage convertedImage = new BufferedImage(resizedImage.getWidth(), resizedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		    convertedImage.createGraphics().drawImage(resizedImage, 0, 0, Color.WHITE, null); // I don't explain it but this line is necessary for png files
		    ImageIO.write(convertedImage, "jpeg", os);

			LOGGER.info("Generated. Getting base64 encoding...", path);
			toReturn = Base64.getEncoder().encodeToString(os.toByteArray());
			if (toReturn == "") { // for whatever reason we couldn't get the base 64 and have an empty String, saw this live once on some previously unsupported filetypes
				throw new Exception();
			}
		} catch (Exception e) {
			LOGGER.error("FAILED to get base64 of \"{}\"", path);
			return Optional.empty();
		}
		LOGGER.info("OK.");
		return Optional.of(toReturn);
	}
}
