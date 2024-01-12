package fr.pan.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;


public class OutputRedirector extends PrintStream {
    private TextArea textArea;
    private PrintStream oldOutputStream;
    
    public OutputRedirector(TextArea textArea, PrintStream out) {
		super(out);
		this.textArea = textArea;
		this.oldOutputStream = out;
	}

    @Override
    public void write(byte[] buf, int off, int len) {
    	//System.out.println("this.getClass()" + this.getClass()); 
        super.write(buf, off, len);

        String msg = new String(buf, off, len);

        Platform.runLater( () -> textArea.appendText(msg));
    }
    

}