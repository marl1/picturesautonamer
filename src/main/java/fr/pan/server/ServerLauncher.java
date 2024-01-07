package fr.pan.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import fr.pan.model.ServerLaunchInfos;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;

public class ServerLauncher {
	
	private static final Logger LOGGER = Logger.getLogger( ServerLauncher.class.getPackage().getName() );

	public static void launch(ServerLaunchInfos serverLaunchInfos) {
		System.out.println(Files.exists(Path.of("llamacpp").normalize().toAbsolutePath()));
	    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "E:\\crea\\java\\PicturesAutoNamer\\llamacpp\\launchLlavaServer.bat");

        processBuilder.redirectInput();
        processBuilder.redirectOutput();
        processBuilder.redirectError();
        processBuilder.redirectErrorStream(true);

        Process process;
		try {
			process = processBuilder.start();
	        String line;
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        try {
		        while ((line = reader.readLine()) != null) {
		        	serverLaunchInfos.getGuiConsoleOutput().setValue(serverLaunchInfos.getGuiConsoleOutput().get()+"\n"+line);
		            System.out.println ("llamacpp: " + line);
		            if(line.startsWith("llama server listening at ")) {
		            	System.out.println("loool");
		            	listFiles(serverLaunchInfos.getFolderToAnalyze());
		            	
		            	//launchQuery();
		            }
		        }
	        	} catch (IOException e) {
					LOGGER.log(Level.WARNING, "Cannot connect to Llamacpp output.");
				}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot start launchLlavaServer.bat.");
		}
	}
	
	private static void listFiles(String folder) {
	    try (Stream<Path> stream = Files.list(Paths.get(folder))) {
	         List<Path> fileList = stream
	          .filter(file -> !Files.isDirectory(file))
	          .toList();
	         for(Path p: fileList) {
	        	 System.out.println(getBase64(p));
	         }
	    } catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
		}
	}
	
	private static String getBase64(Path path) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
			Thumbnails.of(path.toFile())
									.size(300, 300)
									.antialiasing(Antialiasing.ON)
									.toOutputStream(os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (IOException e) {
			throw e;
		}


	}
}
