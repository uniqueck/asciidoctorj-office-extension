package org.uniqueck.asciidoctorj.exceltableconverter;

import org.apache.poi.ss.usermodel.*;
import org.asciidoctor.ast.Column;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.ast.Table;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.uniqueck.asciidoctorj.exceltableconverter.exceptions.AsciidoctorOfficeRuntimeException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("excel")
public class ExcelTableGeneratorBlockMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode structuralNode, String target, Map<String, Object> attributes) {

        List<String> contentLines = new ArrayList<>();
        Map<String, Object> docAttributes = structuralNode.getDocument().getAttributes();

        File excelFile = new File((String) docAttributes.get("docdir"), target);
        if (excelFile.exists()) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(excelFile);
                String sheetName = (String) attributes.get("sheetName");
                if (sheetName == null || sheetName.trim().isEmpty()) {
                    throw new AsciidoctorOfficeRuntimeException("Attribute 'sheetName' is missing");
                } else {
                    Sheet sheet = workbook.getSheet(sheetName);
                    if (sheet != null) {
                        contentLines.add("|===");
                        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                            Row row = sheet.getRow(i);
                            if (row.getRowNum() == 0) {
                                StringBuilder headerRow = new StringBuilder();
                                for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                                    headerRow.append("|" + getCellValue(row, j));
                                }
                                contentLines.add(headerRow.toString());
                                contentLines.add("");
                            } else {
                                for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                                    contentLines.add("|" + getCellValue(row, j));
                                }
                            }
                        }
                        contentLines.add("|===");
                    } else {
                        throw new AsciidoctorOfficeRuntimeException("Sheet with name '" +sheetName + "' couldn't found");
                    }
                }
            } catch (IOException e) {
                throw new AsciidoctorOfficeRuntimeException("Error on opening ExcelFile '" + target + "'",e);
            } finally {
                if (workbook != null ) {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        throw new AsciidoctorOfficeRuntimeException("Error on closing ExcelFile '" + target + "'",e);
                    }
                    workbook = null;
                }
            }

        } else {
            throw new AsciidoctorOfficeRuntimeException("ExcelFile '" + target + "' doesn't exist");
        }


        parseContent(structuralNode, contentLines);
        return null;
    }

    private String getCellValueBasedOnCellType(Cell cell, CellType cellType) {
        switch (cellType) {

            case _NONE:
                return "<none>";
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                return getCellValueBasedOnCellType(cell, cell.getCachedFormulaResultType());
            case BLANK:
                return "";
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case ERROR:
                return "<error>";
        }
        return "";
    }

    private String getCellValue(Row row, int j) {
        Cell cell = row.getCell(j);
        return getCellValueBasedOnCellType(row.getCell(j), cell.getCellType());
    }
}
