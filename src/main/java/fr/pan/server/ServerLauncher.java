package fr.pan.server;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.model.RenamingInfos;
import fr.pan.model.ServerLaunchInfos;
import fr.pan.util.StreamReader;
import fr.pan.util.Renamer;

public class ServerLauncher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerQuerier.class);	
	
	private static Process process;
	
	public static ServerLaunchInfos serverLaunchInfos;

	public static void handleStream(InputStream input) { 
	    try { 
	        int c; 
	        while( (c=input.read())!= -1) { 
	        	System.out.println(c);
	        }
	    } catch(Exception ex) { }
	}

	    
	public static void launch(ServerLaunchInfos serverLaunchInfos) {
		ServerLauncher.serverLaunchInfos = serverLaunchInfos;
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current absolute path is: " + s);
	    ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", currentRelativePath.resolve("llamacpp").resolve("launchLlavaServer.bat").toAbsolutePath().toString());

		try {
			processBuilder.inheritIO();
			process = processBuilder.start();
			TimeUnit.SECONDS.sleep(5);
			FilesProcesser.startFileProcessing();
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Cannot start launchLlavaServer.bat.");
		}
	}

	public static void destroyServerProcess()  {
		if (process == null) {
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
	}
	
	
}
