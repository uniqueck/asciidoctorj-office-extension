package org.uniqueck.asciidoctorj.exceltableconverter;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

class XLSTableConverter extends AbstractExcelConverter<HSSFWorkbook, HSSFSheet, HSSFRow, HSSFCell, HSSFFormulaEvaluator> {

    XLSTableConverter(final File inputFile, final Map<String, Object> attributes) {
        super(inputFile, attributes);
    }

    @Override
    protected HSSFWorkbook open(FileInputStream fis) throws IOException {
        return new HSSFWorkbook(fis);
    }

    @Override
    protected HSSFSheet getSheet(HSSFWorkbook wb, String sheetName) {
        return wb.getSheet(sheetName);
    }

    @Override
    protected HSSFRow getRow(HSSFSheet sheet, int rowIndex) {
        return sheet.getRow(rowIndex);
    }

    @Override
    protected HSSFCell getCell(HSSFRow row, int cellIndex) {
        return row.getCell(cellIndex);
    }

    @Override
    protected HSSFFormulaEvaluator getFormulaEvaluator(final HSSFWorkbook wb) {
        return wb.getCreationHelper().createFormulaEvaluator();
    }

    @Override
    protected String getFillForegroundColorAsHex(final HSSFCell cell) {
        // Get background color
        final HSSFColor cellFillColor = cell.getCellStyle().getFillForegroundColorColor();

        if (cellFillColor.getIndex() != HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex()) {
            final byte[] rgb = new byte[3];
            rgb[0] = (byte) cellFillColor.getTriplet()[0];
            rgb[1] = (byte) cellFillColor.getTriplet()[1];
            rgb[2] = (byte) cellFillColor.getTriplet()[2];
            return encodeHex(rgb);
        }
        return null;
    }
}
