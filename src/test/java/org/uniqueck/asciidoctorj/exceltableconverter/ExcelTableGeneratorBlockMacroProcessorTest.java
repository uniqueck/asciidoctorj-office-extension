package org.uniqueck.asciidoctorj.exceltableconverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uniqueck.asciidoctorj.AbstractAsciidoctorTestHelper;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeFormatNotSupportedRuntimeException;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExcelTableGeneratorBlockMacroProcessorTest extends AbstractAsciidoctorTestHelper {

    @DisplayName("Excel file is missing, so exception should thrown")
    @Test
    void testExcelFileDoesntExist_ThrowsException() {
        AsciidoctorOfficeRuntimeException exception = Assertions.assertThrows(AsciidoctorOfficeRuntimeException.class, () -> convert("excel::missing.xlsx[]"));
        assertEquals("asciidoctor: FAILED: <stdin>: Failed to load AsciiDoc document", exception.getMessage());
        assertEquals("ExcelFile 'missing.xlsx' doesn't exist", exception.getCause().getMessage());
    }

    @DisplayName("Attribute sheetName is missing")
    @Test
    void testExcelFileExistButSheetNameAttributeIsMissing() {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> convert("excel::./simpleExcelTable.xlsx[]"));
        assertEquals("Attribute 'sheetName' is missing", thrown.getCause().getMessage());
    }

    @DisplayName("Sheet with Name doesn't exist")
    @Test
    void testExcelFileExistAndSheetNameIsProvided() {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> convert("excel::./simpleExcelTable.xlsx[sheetName=hugo]"));
        assertEquals("Sheet with name 'hugo' couldn't found", thrown.getCause().getMessage());
    }

    @DisplayName("Sheet with Name exist. With header.")
    @ParameterizedTest
    @ValueSource(strings = {"simpleExcelTable.xls", "simpleExcelTable.xlsx"})
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExistWithHeader(String excelFileName) {
        String actual = convert("excel::" + excelFileName + "[sheetName=Tabelle1,options=\"header\"]");
        String expected = convertFile("src/test/resources/simpleExcelTableHeader.adoc");
        assertEquals(expected, actual);
    }

    @DisplayName("Sheet with Name exist. Without header.")
    @ParameterizedTest
    @ValueSource(strings = {"simpleExcelTable.xls", "simpleExcelTable.xlsx"})
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExistWithoutHeader(String excelFileName) {
        String actual = convert("excel::" + excelFileName + "[sheetName=Tabelle1]");
        String expected = convertFile("src/test/resources/simpleExcelTable.adoc");
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"simpleExcelTable.ods"})
    void testNotSupportedFormats(String excelFileName) {
        AsciidoctorOfficeFormatNotSupportedRuntimeException runtimeException = assertThrows(AsciidoctorOfficeFormatNotSupportedRuntimeException.class, () -> convert("excel::" + excelFileName + "[sheetName=Tabelle1]"));
        assertEquals("Format for file 'simpleExcelTable.ods' currently not supported", runtimeException.getMessage());
    }

    @DisplayName("Sheet with Name exist. Row and col span plus background color.")
    @ParameterizedTest
    @ValueSource(strings = {"rowColSpanBackgroundExcelTable.xls"})
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExistRowColSpanBackgroundXLS(String excelFileName) {
        String actual = convert("excel::" + excelFileName + "[sheetName=Tabelle1]");
        String expected = convertFile("src/test/resources/rowColSpanBackgroundExcelTableXLS.adoc");
        assertEquals(expected, actual);
    }

    @DisplayName("Sheet with Name exist. Row and col span plus background color.")
    @ParameterizedTest
    @ValueSource(strings = {"rowColSpanBackgroundExcelTable.xlsx"})
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExistRowColSpanBackgroundXLSX(String excelFileName) {
        String actual = convert("excel::" + excelFileName + "[sheetName=Tabelle1]");
        String expected = convertFile("src/test/resources/rowColSpanBackgroundExcelTableXLSX.adoc");
        assertEquals(expected, actual);
    }

    @DisplayName("Sheet with Name exist. Hyperlinks.")
    @ParameterizedTest
    @ValueSource(strings = {"hyperlinkExcelTable.xls", "hyperlinkExcelTable.xlsx"})
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExistHyperlink(String excelFileName) {
        String actual = convert("excel::" + excelFileName + "[sheetName=Tabelle1]");
        String expected = convertFile("src/test/resources/hyperlinkExcelTable.adoc");
        assertEquals(expected, actual);
    }

}