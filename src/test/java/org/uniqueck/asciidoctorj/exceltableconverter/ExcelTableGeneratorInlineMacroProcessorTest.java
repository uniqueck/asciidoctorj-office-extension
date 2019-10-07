package org.uniqueck.asciidoctorj.exceltableconverter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.uniqueck.asciidoctorj.AbstractAsciidoctorTestHelper;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelTableGeneratorInlineMacroProcessorTest extends AbstractAsciidoctorTestHelper {

    @DisplayName("Excel file is missing, so exception should thrown")
    @Test
    void testExcelFileDoesntExist_ThrowsException() {
        AsciidoctorOfficeRuntimeException exception = Assertions.assertThrows(AsciidoctorOfficeRuntimeException.class, () -> convert("excel::missing.xlsx[]"));
        assertEquals("asciidoctor: FAILED: <stdin>: Failed to load AsciiDoc document", exception.getMessage());
        assertEquals("ExcelFile 'missing.xlsx' doesn't exist", exception.getCause().getMessage());
    }

    @DisplayName("Attribute sheetName is missing")
    @Test
    void testExcelFileExistButSheetNameAttributeIsMissing(@TempDir File tempDir) {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> convert("excel::./simpleExcelTable.xlsx[]"));
        assertEquals("Attribute 'sheetName' is missing", thrown.getCause().getMessage());
    }

    @DisplayName("Sheet with Name doesn't exist")
    @Test
    void testExcelFileExistAndSheetNameIsProvided(@TempDir File tempDir) {
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> convert("excel::./simpleExcelTable.xlsx[sheetName=hugo]"));
        assertEquals("Sheet with name 'hugo' couldn't found", thrown.getCause().getMessage());
    }

    @DisplayName("Sheet with Name exist")
    @Test
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExist(@TempDir File tempDir) {
        String actual = convert("excel::simpleExcelTable.xlsx[sheetName=Tabelle1]");
        String expected = convertFile("src/test/resources/expectedTableStructure.adoc");
        assertEquals(expected, actual);
    }



}