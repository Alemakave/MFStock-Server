package ru.alemakave.qr.generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class QRGenerator {
    public static BufferedImage generateToBufferedImage(String data) throws WriterException {
        return generateToBufferedImage(data, 100, 100);
    }

    public static BufferedImage generateToBufferedImage(String data, int width, int height) throws WriterException {
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M.toString());
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, "0");

        return generateToBufferedImage(data, width, height, hints);
    }

    public static BufferedImage generateToBufferedImage(String data, int width, int height, Map<EncodeHintType, String> hints) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
