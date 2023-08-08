package ru.alemakave.mfstock.server.servlet.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.eclipse.jetty.http.HttpMethod;
import ru.alemakave.mfstock.generators.NomSerStickerGenerator;
import ru.alemakave.mfstock.server.MFStockServer;
import ru.alemakave.mfstock.server.utils.PageUtils;
import ru.alemakave.slib.servlet.IServletCommand;
import ru.alemakave.slib.utils.PrintUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MFStockGenerateSerSticker implements IServletCommand {
    private final String pagePath = "pages/MFStockNomSerStickerGenerator.html";
    private final InputStream MFSTOCK_NOM_SER_STICKER_GENERATOR_INPUT_STREAM;
    private final String STICKER_FILENAME = "nom_ser_sticker.xlt";
    private final String pageData;

    public MFStockGenerateSerSticker() throws IOException {
        this.MFSTOCK_NOM_SER_STICKER_GENERATOR_INPUT_STREAM = this.getClass().getProtectionDomain().getClassLoader().getResourceAsStream(pagePath);

        if (this.MFSTOCK_NOM_SER_STICKER_GENERATOR_INPUT_STREAM == null) {
            throw new FileNotFoundException(String.format("MFSTOCK_NOM_SER_STICKER_GENERATOR_INPUT_STREAM \"%s\" not found!", pagePath));
        }
        final BufferedInputStream fis = new BufferedInputStream(this.MFSTOCK_NOM_SER_STICKER_GENERATOR_INPUT_STREAM);
        pageData = new String(fis.readAllBytes());
        fis.close();
        this.MFSTOCK_NOM_SER_STICKER_GENERATOR_INPUT_STREAM.close();
    }

    @Override
    public String getCommand() {
        return "mfstock-generate-nom-ser-sticker";
    }

    @Override
    public void call(final HttpMethod method, final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        if (method == HttpMethod.GET) {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getOutputStream().write(pageData.getBytes(StandardCharsets.UTF_8));
        }
        else if (method == HttpMethod.POST && req.getContentType().equals("application/json")) {
            final String data = PageUtils.readAllPostData(req);
            final ObjectMapper mapper = new ObjectMapper();
            final Data dta = mapper.readValue(data, Data.class);
            try {
                final File stickerFile = new File(STICKER_FILENAME);
                new NomSerStickerGenerator().generate(stickerFile, dta.rows.get(0), dta.rows.get(1), dta.rows.get(3));
                PrintUtils.printFile(stickerFile, MFStockServer.props.get("printerName").toString());
                //noinspection ResultOfMethodCallIgnored
                stickerFile.delete();
            }
            catch (WriterException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static class Data {
        public ArrayList<String> rows;
    }
}
