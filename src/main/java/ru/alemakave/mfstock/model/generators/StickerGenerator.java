package ru.alemakave.mfstock.model.generators;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.util.Map;

public abstract class StickerGenerator {
    public static final String TEMPLATE_NOM_SER_STICKER = "templates/r_NomSerStickerMGF.XLT";
    public static final String TEMPLATE_CELL_STICKER = "templates/r_CellSticker.XLT";

    private final InputStream stickerTemplateInputStream;

    public StickerGenerator(InputStream stickerTemplateInputStream) {
        this.stickerTemplateInputStream = stickerTemplateInputStream;
    }

    /**
     * Generate sticker file in Excel format using the template
     *
     * @param outputFile - result file
     * @param dataMap - data map, then key - Cell address (used CellAddress or CellRangeAddress), value - cell data
     */
    public void generate(File outputFile, Map<Object, Object> dataMap) throws IOException {
        Workbook wb = WorkbookFactory.create(stickerTemplateInputStream);
        Sheet sheet = wb.getSheetAt(0);
        for (Object key : dataMap.keySet()) {
            if (key instanceof CellRangeAddress) {
                if (!(dataMap.get(key) instanceof byte[])) {
                    throw new InvalidObjectException("For cell range address available only byte array data!");
                }

                int pictureId = wb.addPicture((byte[])dataMap.get(key), Workbook.PICTURE_TYPE_PNG);
                Drawing<?> picturesDrawer = sheet.createDrawingPatriarch();
                HSSFClientAnchor anchor = new HSSFClientAnchor();
                anchor.setCol1(((CellRangeAddress)key).getFirstColumn());
                anchor.setCol2(((CellRangeAddress)key).getLastColumn());
                anchor.setRow1(((CellRangeAddress)key).getFirstRow());
                anchor.setRow2(((CellRangeAddress)key).getLastRow());
                picturesDrawer.createPicture(anchor, pictureId);
            } else if (key instanceof CellAddress) {
                qwe:
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getAddress().equals(key)) {
                            Object cellData = dataMap.get(key);
                            cell.setCellValue(cellData.toString());

                            break qwe;
                        }
                    }
                }
            } else {
                throw new InvalidObjectException("Unknown key value. Available value instance of CellRangeAddress of CellAddress class!");
            }
        }

        FileOutputStream workbookOutputStream = new FileOutputStream(outputFile);
        wb.write(workbookOutputStream);
        wb.close();
        workbookOutputStream.close();
    }
}
