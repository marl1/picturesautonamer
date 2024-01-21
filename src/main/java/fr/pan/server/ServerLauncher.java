package fr.pan.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.model.RenamingInfos;
import fr.pan.model.ServerLaunchInfos;
import fr.pan.util.Renamer;

public class ServerLauncher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	
	
	private static Process process;

	public static void launch(ServerLaunchInfos serverLaunchInfos) {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current absolute path is: " + s);
	    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", currentRelativePath.resolve("llamacpp").resolve("launchLlavaServer.bat").toAbsolutePath().toString());

        processBuilder.redirectInput();
        processBuilder.redirectOutput();
        processBuilder.redirectError();
        processBuilder.redirectErrorStream(true);

		try {
			process = processBuilder.start();
		} catch (IOException e) {
			LOGGER.error("Cannot start launchLlavaServer.bat.");
		}
		
        String line;
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	        while ((line = reader.readLine()) != null) {
	            LOGGER.info("[llamacpp] {}", line);
	            if(line.startsWith("llama server listening at ")) {
	            	    	List<Path> fileList = listFiles(serverLaunchInfos);
	            	    	LOGGER.info("There is {} image(s) to process.", fileList.size());
	            	    	processFiles(serverLaunchInfos, process, fileList);
	            	     }
	        }
    	} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}


	}

	private static void processFiles(ServerLaunchInfos serverLaunchInfos, Process process, List<Path> fileList) {
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
		 service.submit(() -> destroyServerProcess());
			
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
		    BufferedImage outputImage = Scalr.resize(originalImage, 300);
		    ImageIO.write(outputImage, "jpeg", os);
			LOGGER.info("Generated. Getting base64 encoding...", path);
			toReturn = Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (Exception e) {
			LOGGER.error("FAILED to get base64 of {}", path);
			return Optional.empty();
		}
		LOGGER.info("OK.", path);
		return Optional.of(toReturn);
	}
	
	

	private static void destroyServerProcess()  {
		LOGGER.info("Stopping the llamacpp process...");
		LOGGER.info("Going to stop the process descendants (subprocesses) " + process.descendants().collect(Collectors.toList()));
		process.descendants().forEach((ProcessHandle d) -> {
		    d.destroy();
		});
		LOGGER.info("Going to stop the process " + process);
		process.destroy();
		try {
			process.getErrorStream().close();
			process.getInputStream().close();
			process.getOutputStream().close();
		} catch (IOException e) {
			LOGGER.error("Cannot close the llamacpp streams.");
		}
	}
	
	
}
