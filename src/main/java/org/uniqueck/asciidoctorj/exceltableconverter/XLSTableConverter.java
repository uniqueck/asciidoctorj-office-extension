package org.uniqueck.asciidoctorj.exceltableconverter;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class XLSTableConverter extends AbstractExcelConverter<HSSFWorkbook, HSSFSheet, HSSFRow, HSSFCell, HSSFFormulaEvaluator> {

    XLSTableConverter(File inputFile, String sheetName) {
        super(inputFile, sheetName);
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
    protected HSSFFormulaEvaluator getFormularEvualator(HSSFWorkbook wb) {
        return wb.getCreationHelper().createFormulaEvaluator();
    }
}
