package fr.pan.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.controller.MainController;
import fr.pan.model.UserGuiInfos;
import fr.pan.util.FilesProcesser;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ServerLauncher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	
	
	private static Process process;

	public static void handleStream(InputStream input) { 
	    try { 
	        int c; 
	        while( (c=input.read())!= -1) { 
	        	System.out.println(c);
	        }
	    } catch(Exception ex) { }
	}

	    
	public static void launch(UserGuiInfos userGuiInfos) {
		if (process != null && process.isAlive()) {
			LOGGER.error("Error: you seem to be trying to launch a LlamaCpp server but the previous one is still alive. "
					+ "Please close this app and kill the program \"server\" in your task manager.");
			return;
		}

		Path currentRelativePath = Paths.get("");
		LOGGER.info("Current absolute path is: " + currentRelativePath.toAbsolutePath().toString());
		
		String llamaCppServerPath;
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			llamaCppServerPath = currentRelativePath.resolve("llamacpp").resolve("launchLlavaServer.bat")
					.toAbsolutePath().toString();
		    processBuilder.command("cmd.exe", "/C", llamaCppServerPath);
			LOGGER.info("Launching llamaCpp server from: " + llamaCppServerPath);
		} else {
			llamaCppServerPath = currentRelativePath.resolve("llamacpp").resolve("linux/launchLlavaServer.sh")
					.toAbsolutePath().toString();
			processBuilder.command(llamaCppServerPath);
			LOGGER.info("Launching llamaCpp server from: " + llamaCppServerPath);
		}
		

		try {
			processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
			    LOGGER.info("[server] " + line);
		        if(line.contains("HTTP server listening")) {
		    		Task<Boolean> task = new Task<Boolean>() {
		    		    @Override
		    		    protected Boolean call() throws Exception {
			        		new FilesProcesser().startFileProcessing(userGuiInfos);
		    				return true;
		    		    }

		    		};
		    		new Thread(task).start();
			     }
			}
			reader.close();
		} catch (IOException e) {
			LOGGER.error("Cannot start {}", llamaCppServerPath, e);
		}
	}

	public static void destroyServerProcess()  {
		if (process == null || !process.isAlive()) { // process wasn't even launched, we quit
			return;
		}
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
		
		if (!process.isAlive()) {
			LOGGER.info("Llamacpp server process stopped.");
		} else {
			LOGGER.error("Llamacpp doesn't seems to have stopped properly. Please check if the program \"server\" is running in your task manager.");
		}
		
	}
	
	
}
