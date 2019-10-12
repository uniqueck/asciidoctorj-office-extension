package org.uniqueck.asciidoctorj.exceltableconverter;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter(AccessLevel.PROTECTED)
abstract class AbstractExcelConverter<WORKBOOK extends Workbook, SHEET extends Sheet, ROW extends Row, CELL extends Cell, FORMULAREVUALATOR extends FormulaEvaluator> {

    private final File inputFile;
    private final String sheetName;
    private final DataFormatter dataFormatter;

    AbstractExcelConverter(File inputFile, String sheetName) {
        if (inputFile == null || !inputFile.exists()) {
            throw new AsciidoctorOfficeRuntimeException("ExcelFile '"+(inputFile != null ? inputFile.getName() : null) +"' doesn't exist");
        }
        this.inputFile = inputFile;
        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new AsciidoctorOfficeRuntimeException("Attribute 'sheetName' is missing");
        }
        this.sheetName = sheetName;
        this.dataFormatter = new DataFormatter();
    }


    protected abstract WORKBOOK open(FileInputStream fis) throws IOException;
    protected abstract SHEET getSheet(WORKBOOK wb, String sheetName);
    protected abstract ROW getRow(SHEET sheet, int rowIndex);
    protected abstract CELL getCell(ROW row, int cellIndex);
    protected abstract FORMULAREVUALATOR getFormularEvualator(WORKBOOK wb);


    protected String getCellValue(CELL cell, FORMULAREVUALATOR formularEvualator) {
        return getDataFormatter().formatCellValue(cell, formularEvualator);
    }

    public List<String> convert() {

        List<String> contentLines = new ArrayList<>();

        DataFormatter dataFormatter = new DataFormatter();

        WORKBOOK workbook = null;
        SHEET sheet = null;
        FileInputStream fis = null;
        if (getInputFile().exists()) {

            try {
                workbook = open(fis = new FileInputStream(getInputFile()));
                FORMULAREVUALATOR formularEvaluator = getFormularEvualator(workbook);
                if (sheetName == null || sheetName.trim().isEmpty()) {
                    throw new AsciidoctorOfficeRuntimeException("Attribute 'sheetName' is missing");
                } else {
                    sheet = getSheet(workbook, getSheetName());
                    if (sheet != null) {
                        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                            ROW row = getRow(sheet, i);
                            if (row.getRowNum() == 0) {
                                short cellNums = row.getLastCellNum();
                                Integer width[] = new Integer[cellNums];
                                for (int cellNumIndex = 0; cellNumIndex < cellNums; cellNumIndex++) {
                                    width[cellNumIndex] = sheet.getColumnWidth(cellNumIndex);
                                }
                                int sum = Arrays.stream(width).mapToInt(Integer::intValue).sum();
                                String cols = Arrays.stream(width).map(it -> Math.round((100 * it) / sum)).map(it -> it.toString()).collect(Collectors.joining(","));
                                contentLines.add("[options=header, cols=\"" + cols + "\"]");
                                contentLines.add("|===");
                            }

                            for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
                                CELL cell = getCell(row, cellIndex);
                                String cellValue = getCellValue(cell, formularEvaluator);

                                String cellStyle = "";
                                switch (cell.getCellStyle().getAlignment()) {
                                    case RIGHT:
                                        cellStyle = cellStyle.concat(">");
                                        break;
                                    case CENTER:
                                        cellStyle = cellStyle.concat("^");
                                        break;
                                }

                                switch (cell.getCellStyle().getVerticalAlignment()) {
                                    case BOTTOM:
                                        cellStyle = cellStyle.concat(".>");
                                        break;
                                    case CENTER:
                                        cellStyle = cellStyle.concat(".^");
                                        break;
                                }

                                contentLines.add(cellStyle + "|" + cellValue);
                            }
                        }
                        contentLines.add("|===");
                    } else {
                        throw new AsciidoctorOfficeRuntimeException("Sheet with name '" + sheetName + "' couldn't found");
                    }
                }

                workbook.close();
                fis.close();
            } catch (IOException e) {
                throw new AsciidoctorOfficeRuntimeException("Error on opening ExcelFile '" + getInputFile().getName() + "'", e);
            }

        } else {
            throw new AsciidoctorOfficeRuntimeException("ExcelFile '" + getInputFile().getName() + "' doesn't exist");
        }

        return contentLines;


    }

}
