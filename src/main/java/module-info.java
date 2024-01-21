open module fr.pan {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires java.logging;
	requires java.net.http;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires static lombok;
	requires javafx.base;
	requires java.desktop;
	requires org.slf4j;
	requires ch.qos.logback.core;
	requires org.apache.commons.text;
	requires imgscalr.lib;
	exports fr.pan.main;
	exports fr.pan.controller;
}