package fr.pan.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.pan.model.ServerLaunchInfos;
import javafx.concurrent.Task;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;

public class ServerLauncher {
	
	private static final Logger LOGGER = Logger.getLogger(ServerLauncher.class.getPackage().getName());
	
	private static Process process;

	public static void launch(ServerLaunchInfos serverLaunchInfos) {

	    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "E:\\crea\\java\\PicturesAutoNamer\\llamacpp\\launchLlavaServer.bat");

        processBuilder.redirectInput();
        processBuilder.redirectOutput();
        processBuilder.redirectError();
        processBuilder.redirectErrorStream(true);

		try {
			process = processBuilder.start();
	        String line;
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        try {
		        while ((line = reader.readLine()) != null) {
		            System.out.println ("[llamacpp] " + line);
		            if(line.startsWith("llama server listening at ")) {
		            	    	List<Path> fileList = listFiles(serverLaunchInfos);
		            	    	System.out.println("There is " + fileList.size() + " image(s) to process.");
		            			System.out.println("Going to stop the process descendants (subprocesses) " + process.descendants().collect(Collectors.toList()));

		            	    	processFiles(serverLaunchInfos, process, fileList);
		            	     }
		        }
	        	} catch (IOException e) {
					LOGGER.log(Level.WARNING, "Cannot connect to Llamacpp output.");
				}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot start launchLlavaServer.bat.");
		}

	}

	private static void processFiles(ServerLaunchInfos serverLaunchInfos, Process process, List<Path> fileList) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		 for(Path p: fileList) {
			service.submit(() -> ServerQuerier.launchQuery(getImgInBase64(p), serverLaunchInfos.getPrompt()));
		 }
		 service.submit(() -> destroyServerProcess());
			
	}
	
	private static List<Path> listFiles(ServerLaunchInfos serverLaunchInfos) throws IOException {
	    try (Stream<Path> stream = Files.list(Paths.get(serverLaunchInfos.getFolderToAnalyze()))) {
	         return stream
	        		 	.filter(file -> !Files.isDirectory(file))
	        		 	.toList();
	    } catch (IOException e) {
			throw e;
		}
	}
	
	private static String getImgInBase64(Path path) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
			Thumbnails.of(path.toFile())
									.size(300, 300)
									.antialiasing(Antialiasing.ON)
									.toOutputStream(os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (IOException e) {
			return null; //TODO
		}
	}

	private static void destroyServerProcess()  {
		System.out.println("destroy !!!");
		System.out.println("Going to stop the process descendants (subprocesses) " + process.descendants().collect(Collectors.toList()));
		process.descendants().forEach((ProcessHandle d) -> {
		    d.destroy();
		});
		System.out.println("Going to stop the process " + process);
		process.destroy();
		try {
			process.getErrorStream().close();
			process.getInputStream().close();
			process.getOutputStream().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
	}
	
	
}
