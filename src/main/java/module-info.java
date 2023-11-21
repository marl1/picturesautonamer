module pan {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires java.logging;
	requires java.net.http;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires lombok;
	opens fr.pan.main;
	exports fr.pan.main;
}