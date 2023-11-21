package fr.pan.controller;

import java.awt.MenuBar;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import fr.pan.server.ServerLauncher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class MainController {
	
	@FXML
	private VBox root;

	@FXML
	private TextField inputFolder;
	
	
	@FXML
	private void chooseFolderClicked() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog((Stage)root.getScene().getWindow());

		if(selectedDirectory != null)
			inputFolder.textProperty().set(selectedDirectory.getAbsolutePath());
	}
	
	@FXML
	private void startConversionClicked() throws IOException {
		ServerLauncher.launch();
	}

	
}

