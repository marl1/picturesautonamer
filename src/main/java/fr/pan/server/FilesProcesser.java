package fr.pan.server;

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
import fr.pan.model.ServerLaunchInfos;
import fr.pan.util.Renamer;

public class FilesProcesser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FilesProcesser.class);	
	
	public static void startFileProcessing() {
    	List<Path> fileList = listFiles(ServerLauncher.serverLaunchInfos);
    	LOGGER.info("There is {} image(s) to process.", fileList.size());
    	processFiles(ServerLauncher.serverLaunchInfos, fileList);
	}
	
	private static void processFiles(ServerLaunchInfos serverLaunchInfos, List<Path> fileList) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		List<RenamingInfos> renamingInfosList = new ArrayList<>();
		 for(Path p: fileList) {
			 LOGGER.info("Processing {}...", p);
			 
			 Optional<String> imgBase64 = getImgInBase64(p);
			 if(imgBase64.isPresent()) {
				 String newName = ServerQuerier.launchQuery(imgBase64.get(), serverLaunchInfos.getPrompt());
					renamingInfosList.add(new RenamingInfos(p, newName));
		 	 }

		 }
		 System.out.println(renamingInfosList.get(0).getOldPath());
		 System.out.println(renamingInfosList.get(0).getOldPath().getParent());
		 System.out.println(renamingInfosList.get(0).getNewFileName());
		 service.submit(() -> Renamer.rename(renamingInfosList));
		 service.submit(() -> ServerLauncher.destroyServerProcess());
			
	}
	
	private static List<Path> listFiles(ServerLaunchInfos serverLaunchInfos) {
		List<Path> listToReturn = new ArrayList<>();
	    try (Stream<Path> stream = Files.list(Paths.get(serverLaunchInfos.getFolderToAnalyze()))) {
	    	listToReturn.addAll(stream
	        		 				.filter(file -> !Files.isDirectory(file))
	        		 				.toList());
	    } catch (IOException e) {
			LOGGER.error("FAILED to read the folder {}", serverLaunchInfos.getFolderToAnalyze());
		}
	    return listToReturn;
	}
	
	private static Optional<String> getImgInBase64(Path path) {
		String toReturn;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
			LOGGER.info("Generating thumbnail for {}...", path);
			BufferedImage originalImage = ImageIO.read(path.toFile());
		    BufferedImage resizedImage = Scalr.resize(originalImage, 15);

		    LOGGER.info("Converting...");
		    final BufferedImage convertedImage = new BufferedImage(resizedImage.getWidth(), resizedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		    convertedImage.createGraphics().drawImage(resizedImage, 0, 0, Color.WHITE, null); // I don't explain it but this line is necessary for png files
		    ImageIO.write(convertedImage, "jpeg", os);

			LOGGER.info("Generated. Getting base64 encoding...", path);
			toReturn = Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (Exception e) {
			LOGGER.error("FAILED to get base64 of {}", path);
			return Optional.empty();
		}
		LOGGER.info("OK.");
		return Optional.of(toReturn);
	}
}
