package fr.pan.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerLauncher {
	
	private static final Logger LOGGER = Logger.getLogger( ServerLauncher.class.getPackage().getName() );

	public static void launch() {
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
		            System.out.println ("llamacpp: " + line);
		            if(line.startsWith("llama server listening at ")) {
		            	System.out.println("loool");
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
}
