package ru.alemakave.slib.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    // convert BufferedImage to byte[]
    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();
    }

    // Copied from https://stackoverflow.com/questions/9417356/bufferedimage-resize
    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        Image tmpImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
        BufferedImage resultImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = resultImage.createGraphics();
        graphics.drawImage(tmpImage, 0, 0, null);
        graphics.dispose();

        return resultImage;
    }
}
