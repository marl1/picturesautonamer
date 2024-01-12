package fr.pan.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import fr.pan.model.ServerLaunchInfos;
import fr.pan.server.ServerLauncher;
import fr.pan.utils.OutputRedirector;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {
	
	@FXML
	private VBox root;

	@FXML
	private TextField inputFolder;
	
	@FXML
	private TextArea prompt;
	
	@FXML
	private TextArea consoleOutput;
	
    @FXML
    public void initialize() {
    	System.setOut(new OutputRedirector(consoleOutput, System.out));
    }
	
	@FXML
	private void chooseFolderClicked() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog((Stage)root.getScene().getWindow());

		if(selectedDirectory != null)
			inputFolder.textProperty().set(selectedDirectory.getAbsolutePath());
	}
	
	@FXML
	private void startConversionClicked() throws IOException {
		
		Task<Boolean> task = new Task<Boolean>() {

		    @Override
		    protected Boolean call() throws Exception {
				ServerLauncher.launch(new ServerLaunchInfos(inputFolder.getText(), prompt.getText()));
				return true;
		    }

		};
		new Thread(task).start();
	}
	
}

