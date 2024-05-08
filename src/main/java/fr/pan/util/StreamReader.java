package fr.pan.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pan.server.FilesProcesser;
import fr.pan.server.ServerQuerier;

public class StreamReader implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamReader.class);	

    String name;
    InputStream is;
    Thread thread;      
    public StreamReader(String name, InputStream is) {
        this.name = name;
        this.is = is;
    }       
    public void start () {
        thread = new Thread (this);
        thread.start ();
    }       
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);   
            while (true) {
                String s = br.readLine ();
                if (s == null) break;
                //System.out.println ("[" + name + "] " + s);
		        if(s.contains("HTTP server listening")) {
		        	FilesProcesser.startFileProcessing();
			     }
            }
            is.close ();    
        } catch (Exception ex) {
            System.out.println ("Problem reading stream " + name + "... :" + ex);
            ex.printStackTrace ();
        }
    }
}
