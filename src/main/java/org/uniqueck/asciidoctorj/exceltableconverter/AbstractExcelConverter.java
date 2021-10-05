package org.uniqueck.asciidoctorj.exceltableconverter;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.uniqueck.asciidoctorj.Util;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter(AccessLevel.PROTECTED)
abstract class AbstractExcelConverter<WORKBOOK extends Workbook, SHEET extends Sheet, ROW extends Row, CELL extends Cell, FORMULAEVALUATOR extends FormulaEvaluator> {

    private final Map<String, Object> attributes;
    private final File inputFile;
    private final String sheetName;
    private final DataFormatter dataFormatter;

    AbstractExcelConverter(final File inputFile, final Map<String, Object> attributes) {
        this.attributes = attributes;

        if (inputFile == null || !inputFile.exists()) {
            throw new AsciidoctorOfficeRuntimeException("ExcelFile '" + (inputFile != null ? inputFile.getName() : null) + "' doesn't exist");
        }
        this.inputFile = inputFile;

        final String sheetName = getSheetName(attributes);

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
    protected abstract FORMULAEVALUATOR getFormulaEvaluator(WORKBOOK wb);
    protected abstract String getFillForegroundColorAsHex(CELL cell);

    protected String getCellValue(CELL cell, FORMULAEVALUATOR formularEvaluator) {
        return getDataFormatter().formatCellValue(cell, formularEvaluator);
    }

    public List<String> convert() {
        final List<String> contentLines = new ArrayList<>();

        FileInputStream fis = null;
        if (getInputFile().exists()) {
            try {
                final WORKBOOK workbook = open(fis = new FileInputStream(getInputFile()));
                FORMULAEVALUATOR formulaEvaluator = getFormulaEvaluator(workbook);
                if (sheetName == null || sheetName.trim().isEmpty()) {
                    throw new AsciidoctorOfficeRuntimeException("Attribute 'sheetName' is missing");
                } else {
                    final SHEET sheet = getSheet(workbook, getSheetName());
                    if (sheet != null) {
                        // Inspired from https://github.com/docToolchain/docToolchain/blob/master/scripts/exportExcel.gradle
                        boolean headerCreated = false;

                        // Find maximum column index
                        int colCount = 0;
                        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                            final ROW row = getRow(sheet, i);
                            if (row != null) {
                                colCount = Math.max(colCount, row.getLastCellNum());
                            }
                        }

                        // Find regions
                        final int regionCount = sheet.getNumMergedRegions();
                        final List<CellRangeAddress> regions = new ArrayList<>();
                        for (int regionIndex = 0; regionIndex < regionCount; regionIndex++) {
                            regions.add(sheet.getMergedRegion(regionIndex));
                        }

                        boolean cellBackgroundColorReset = false;

                        // Process rows and columns
                        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                            final ROW row = getRow(sheet, rowIndex);

                            if (row != null) {
                                if (!headerCreated) {
                                    headerCreated = true;

                                    final Integer width[] = new Integer[colCount];
                                    for (int colIndex = 0; colIndex < colCount; colIndex++) {
                                        width[colIndex] = sheet.getColumnWidth(colIndex);
                                    }
                                    final int widthSum = Arrays.stream(width).mapToInt(Integer::intValue).sum();
                                    final String cols = Arrays.stream(width).map(it -> Math.round((100 * it) / widthSum)).map(Object::toString).collect(Collectors.joining(","));

                                    final Map<String, Object> tableAttributes  = new HashMap<>();
                                    tableAttributes.putAll(attributes);
                                    tableAttributes.remove("sheetName");

                                    final List<String> tableAttributesList = new ArrayList<>();
                                    for (Map.Entry<String, Object> entry : tableAttributes.entrySet()) {
                                        tableAttributesList.add(entry.getKey() + "=\"" + entry.getValue().toString() + "\"");
                                    }

                                    if (tableAttributesList.isEmpty()) {
                                        contentLines.add("[cols=\"" + cols + "\"]");
                                    } else {
                                        contentLines.add("[cols=\"" + cols + "\", " + StringUtils.join(tableAttributesList, ", ") + "]");
                                    }
                                    contentLines.add("|===");
                                }

                                // Check if row contains any cells
                                if (row.getFirstCellNum() >= 0) {
                                    for (int cellIndex = row.getFirstCellNum(); cellIndex < colCount; cellIndex++) {
                                        final CELL cell = getCell(row, cellIndex);

                                        // Get and process cell value
                                        String cellValue = getCellValue(cell, formulaEvaluator);
                                        if (cellValue.startsWith("*") && cellValue.endsWith("\u20AC")) {
                                            // Remove special characters at currency
                                            cellValue = cellValue.substring(1).trim();
                                        }

                                        if (cell != null) {
                                            // Check for hyperlinks
                                            final Hyperlink cellHyperlink = cell.getHyperlink();
                                            if (cellHyperlink != null && cellHyperlink.getAddress() != null && !cellHyperlink.getAddress().isEmpty()) {
                                                // Only set explicit hyperlink if not a URL is set anyway
                                                if (!cellHyperlink.getAddress().equals(cellValue)) {
                                                    cellValue = cellHyperlink.getAddress() + "[" + cellValue + "]";
                                                }
                                            }

                                            final CellRangeAddress cellRegion = regions.stream().filter(it -> it.isInRange(cell)).findFirst().orElse(null);
                                            final StringBuilder cellStyle = new StringBuilder();
                                            boolean cellSkip = false;

                                            // Process regions
                                            if (cellRegion != null) {
                                                // Check if cell is in the upper left corner of the region
                                                if (cellRegion.getFirstRow() == cell.getRowIndex() && cellRegion.getFirstColumn() == cell.getColumnIndex()) {
                                                    final int cellRowSpan = 1 + cellRegion.getLastColumn() - cellRegion.getFirstColumn();
                                                    final int cellColSpan = 1 + cellRegion.getLastRow() - cellRegion.getFirstRow();
                                                    if (cellRowSpan > 1) {
                                                        cellStyle.append(cellRowSpan);
                                                    }
                                                    if (cellColSpan > 1) {
                                                        cellStyle.append(".").append(cellColSpan);
                                                    }
                                                    cellStyle.append("+");
                                                } else {
                                                    cellSkip = true;
                                                }
                                            }

                                            if (!cellSkip) {
                                                String cellBackgroundColor = null;
                                                if (cell.getCellStyle() != null) {
                                                    // Get horizontal alignment
                                                    if (cell.getCellStyle().getAlignment() != null) {
                                                        switch (cell.getCellStyle().getAlignment()) {
                                                            case RIGHT:
                                                                cellStyle.append(">");
                                                                break;
                                                            case CENTER:
                                                                cellStyle.append("^");
                                                                break;
                                                        }
                                                    }

                                                    // Get vertical alignment
                                                    if (cell.getCellStyle().getVerticalAlignment() != null) {
                                                        switch (cell.getCellStyle().getVerticalAlignment()) {
                                                            case BOTTOM:
                                                                cellStyle.append(".>");
                                                                break;
                                                            case CENTER:
                                                                cellStyle.append(".^");
                                                                break;
                                                        }
                                                    }

                                                    // Specify font style
                                                    final int cellFontIndex = cell.getCellStyle().getFontIndexAsInt();
                                                    final Font cellFont = workbook.getFontAt(cellFontIndex);
                                                    if (cellFont != null) {
                                                        if (cellFont.getBold()) {
                                                            cellStyle.append("s");
                                                        }
                                                        if (cellFont.getItalic()) {
                                                            cellStyle.append("e");
                                                        }
                                                    }

                                                    // Get background color
                                                    cellBackgroundColor = getFillForegroundColorAsHex(cell);
                                                }

                                                // Add the actual cell
                                                contentLines.add(cellStyle + "|" + cellValue);

                                                // Set the color definition or clean up after defining cell
                                                if (cellBackgroundColor != null && !cellBackgroundColor.isEmpty()) {
                                                    contentLines.add("{set:cellbgcolor:#" + cellBackgroundColor + "}");
                                                    cellBackgroundColorReset = true;
                                                } else if (cellBackgroundColorReset) {
                                                    contentLines.add("{set:cellbgcolor\\!}");
                                                    cellBackgroundColorReset = false;
                                                }
                                            }
                                        } else {
                                            // Add empty cell
                                            contentLines.add("|");

                                            // Clean up color definitions after defining cell
                                            if (cellBackgroundColorReset) {
                                                contentLines.add("{set:cellbgcolor\\!}");
                                                cellBackgroundColorReset = false;
                                            }
                                        }
                                    }

                                    // Add empty row for separation
                                    contentLines.add("");
                                }
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

    protected static String getSheetName(final Map<String, Object> attributes) {
        return Util.getStringAttributeValue(attributes, "sheetName");
    }

    protected static String encodeHex(final byte[] bytes) {
        final StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

}
