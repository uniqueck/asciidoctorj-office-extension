package org.uniqueck.asciidoctorj.exceltableconverter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

class XLSXTableConverter extends AbstractExcelConverter<XSSFWorkbook, XSSFSheet, XSSFRow, XSSFCell, XSSFFormulaEvaluator> {

    XLSXTableConverter(final File inputFile, final Map<String, Object> attributes) {
        super(inputFile, attributes);
    }

    @Override
    protected XSSFWorkbook open(FileInputStream fis) throws IOException {
        return new XSSFWorkbook(fis);
    }

    @Override
    protected XSSFRow getRow(XSSFSheet sheet, int rowIndex) {
        return sheet.getRow(rowIndex);
    }

    @Override
    protected XSSFSheet getSheet(XSSFWorkbook wb, String sheetName) {
        return wb.getSheet(sheetName);
    }

    @Override
    protected XSSFCell getCell(XSSFRow row, int cellIndex) {
        return row.getCell(cellIndex);
    }

    @Override
    protected XSSFFormulaEvaluator getFormulaEvaluator(XSSFWorkbook wb) {
        return wb.getCreationHelper().createFormulaEvaluator();
    }

    @Override
    protected String getFillForegroundColorAsHex(final XSSFCell cell) {
        // Get background color
        final XSSFColor cellFillColor = cell.getCellStyle().getFillForegroundColorColor();
        if (cellFillColor != null) {
            return encodeHex(cellFillColor.getRGB());
        }
        return null;
    }

}
