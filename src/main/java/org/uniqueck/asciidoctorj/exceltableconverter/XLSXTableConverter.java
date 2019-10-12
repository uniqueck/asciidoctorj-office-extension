package org.uniqueck.asciidoctorj.exceltableconverter;

import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class XLSXTableConverter extends AbstractExcelConverter<XSSFWorkbook, XSSFSheet, XSSFRow, XSSFCell, XSSFFormulaEvaluator> {

    XLSXTableConverter(File inputFile, String sheetName) {
        super(inputFile, sheetName);
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
    protected XSSFFormulaEvaluator getFormularEvualator(XSSFWorkbook wb) {
        return wb.getCreationHelper().createFormulaEvaluator();
    }
}
