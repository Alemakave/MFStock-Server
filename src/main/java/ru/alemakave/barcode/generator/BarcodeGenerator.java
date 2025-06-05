package ru.alemakave.barcode.generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import ru.alemakave.barcode.exception.UnsupportedBarcode;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static ru.alemakave.slib.utils.ImageUtils.resize;

public final class BarcodeGenerator {
    public static BufferedImage generateBufferedImage(BarcodeFormat barcodeFormat, String data) throws WriterException {
        switch (barcodeFormat) {
            case CODE_39:
            case CODE_93:
            case CODE_128:
            case CODABAR:
            case EAN_8:
            case EAN_13:
            case ITF:
            case PDF_417:
                return generateBufferedImage(barcodeFormat, data, 100, 30);
            case UPC_A: // Только цифры
            case UPC_E: // Только цифры
            case MAXICODE:
            case RSS_14:
            case UPC_EAN_EXTENSION:
                throw new UnsupportedBarcode("Данный формат баркода не поддерживается");
            case QR_CODE:
                return resize(generateBufferedImage(barcodeFormat, data, 1000, 1000), 100, 100);
            default:
                return generateBufferedImage(barcodeFormat, data, 100, 100);
        }
    }

    public static BufferedImage generateBufferedImage(BarcodeFormat barcodeFormat, String data, int width, int height) throws WriterException {
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        if (barcodeFormat == BarcodeFormat.QR_CODE) {
            hints.put(EncodeHintType.MARGIN, "0");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M.toString());
        }

        return generateBufferedImage(barcodeFormat, data, width, height, hints);
    }

    public static BufferedImage generateBufferedImage(BarcodeFormat barcodeFormat, String data, int width, int height, Map<EncodeHintType, String> hints) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(data, barcodeFormat, width, height, hints);

        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
