package fr.pan.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.logging.OutputRedirector;
import fr.pan.model.UserGuiInfos;
import fr.pan.server.ServerLauncher;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AboutController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutController.class);
    
	@FXML
	private Pane root;

	@FXML
	private void openLlamaCppContribPage() throws IOException {
		this.openWebsite("https://github.com/ggerganov/llama.cpp/graphs/contributors");
	}
	
	@FXML
	private void openLlavaPage() throws IOException {
		this.openWebsite("https://llava-vl.github.io/");
	}
	
	@FXML
	private void closeAbout() throws IOException {
		
		((Stage) root.getScene().getWindow()).close();
	}
	
	private void openWebsite(String url) {
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				Desktop.getDesktop().browse(new URL(url).toURI());
			}else if (Runtime.getRuntime().exec(new String[] { "which", "xdg-open" }).getInputStream().read() != -1) {
	                Runtime.getRuntime().exec(new String[] { "xdg-open", url });
		    } else {
		    	LOGGER.info("No way to open {}", url);
		    }
		} catch (IOException | URISyntaxException e) {
			LOGGER.error("Error trying to open {}", url, e);
		}
	}
}

