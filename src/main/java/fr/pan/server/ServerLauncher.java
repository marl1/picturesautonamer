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

import fr.pan.model.UserGuiInfos;
import fr.pan.util.FilesProcesser;

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
		
		String llamaCppServerPath = currentRelativePath.resolve("llamacpp").resolve("launchLlavaServer.bat").toAbsolutePath().toString();
		LOGGER.info("Launching llamaCpp server from: " + llamaCppServerPath);
	    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", llamaCppServerPath);

		try {
			processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
			    LOGGER.info("[server] " + line);
		        if(line.contains("HTTP server listening")) {
		        	new Thread(() -> {
		        		new FilesProcesser().startFileProcessing(userGuiInfos);
		        	}).start();
			     }
			}
			reader.close();
		} catch (IOException e) {
			LOGGER.error("Cannot start launchLlavaServer.bat.");
		}
	}

	public static void destroyServerProcess()  {
		if (process == null) { // process wasn't even launched, we quit
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
