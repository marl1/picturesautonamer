package fr.pan.controller;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.logging.OutputRedirector;
import fr.pan.model.UserGuiInfos;
import fr.pan.server.ServerLauncher;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@FXML
	private VBox root;

	@FXML
	private TextField inputFolder;
	
	@FXML
	private TextArea prompt;
	
	@FXML
	private TextArea consoleOutput;
	
	@FXML
	private Button analyseConvertButton;
	
	@FXML
	private CheckBox includeSubdirectories;	

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label currentFile;

	@FXML
	private Text numberProgress;
	
    @FXML
    public void initialize() {
    	System.setOut(new OutputRedirector(consoleOutput, System.out));
    	LOGGER.info("Init GUI...");
    	analyseConvertButton.disableProperty().bind(
    		    Bindings.isEmpty(inputFolder.textProperty())
    		);
    }

	@FXML
	private void chooseFolderClicked() throws IOException {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog((Stage)root.getScene().getWindow());

		if(selectedDirectory != null) {
			inputFolder.textProperty().set(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	private void startConversionClicked() throws IOException {
		
		Task<Boolean> task = new Task<Boolean>() {
		    @Override
		    protected Boolean call() throws Exception {
				ServerLauncher.launch(new UserGuiInfos(MainController.this, inputFolder.getText(), prompt.getText(), includeSubdirectories.isSelected()));
				return true;
		    }

		};
		new Thread(task).start();
	}

	public void updateProgress(double progress, String currentPath, String numberProgress) {
		this.progressBar.setProgress(progress);
		this.currentFile.setText("Finding a name for \"" + currentPath + "\"...");
		this.numberProgress.setText(numberProgress + " done.");
	}

	public void indicateRenaming() {
		this.progressBar.setProgress(0.99);
		this.currentFile.setText("");
		this.numberProgress.setText("Renaming files...");
	}
	
	public void indicateFinished() {
		this.progressBar.setProgress(1);
		this.currentFile.setText("");
		this.numberProgress.setText("Finished.");
	}
}

