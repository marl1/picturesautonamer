module com.twelvemonkeys.imageio.webp {
    requires transitive java.desktop;

    exports com.twelvemonkeys.imageio.plugins.webp;
    exports com.twelvemonkeys.imageio.plugins.webp.lossless;
    exports com.twelvemonkeys.imageio.plugins.webp.vp8;

    provides javax.imageio.spi.ImageReaderSpi with
        com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;

}
