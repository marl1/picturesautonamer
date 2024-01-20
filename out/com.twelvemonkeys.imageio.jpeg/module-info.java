module com.twelvemonkeys.imageio.jpeg {
    requires java.xml;

    requires transitive java.desktop;

    exports com.twelvemonkeys.imageio.plugins.jpeg;

    provides javax.imageio.spi.ImageReaderSpi with
        com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi;
    provides javax.imageio.spi.ImageWriterSpi with
        com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriterSpi;

}


		<dependency>
			<groupId>com.twelvemonkeys.imageio</groupId>
			<artifactId>imageio-jpeg</artifactId>
			<version>3.10.1</version>

Error: Modules plexus.io and plexus.archiver export package org.codehaus.plexus.components.io.resources to module maven.archiver