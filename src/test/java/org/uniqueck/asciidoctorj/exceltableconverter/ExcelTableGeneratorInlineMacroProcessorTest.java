package org.uniqueck.asciidoctorj.exceltableconverter;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExcelTableGeneratorInlineMacroProcessorTest {

    @DisplayName("Excel file is missing, so exception should thrown")
    @Test
    void testExcelFileDoesntExist_ThrowsException() {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
        Assertions.assertThrows(RuntimeException.class, () -> asciidoctor.convert("excel::test.xlsx[]", OptionsBuilder.options().toFile(false).get()));
    }

    @DisplayName("Attribute sheetName is missing")
    @Test
    void testExcelFileExistButSheetNameAttributeIsMissing(@TempDir File tempDir) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> asciidoctor.convertFile(new File("./src/test/resources/attributeSheetNameIsMissing.adoc"), OptionsBuilder.options().baseDir(new File("src/test/resources")).inPlace(true).safe(SafeMode.UNSAFE).backend("html5").toFile(false).get()));
        assertEquals("Attribute 'sheetName' is missing", thrown.getCause().getMessage());
    }

    @DisplayName("Sheet with Name doesn't exist")
    @Test
    void testExcelFileExistAndSheetNameIsProvided(@TempDir File tempDir) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> asciidoctor.convertFile(new File("./src/test/resources/attributeSheetNameIsProvided.adoc"), OptionsBuilder.options().baseDir(new File("src/test/resources")).inPlace(true).safe(SafeMode.UNSAFE).backend("html5").toFile(false).get()));
        assertEquals("Sheet with name 'hugo' couldn't found", thrown.getCause().getMessage());
    }

    @DisplayName("Sheet with Name exist")
    @Test
    void testExcelFileExistAndSheetNameIsProvidedAndSheetExist(@TempDir File tempDir) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
        String content = asciidoctor.convertFile(new File("./src/test/resources/attributeSheetNameIsProvidedAndIsCorrect.adoc"), OptionsBuilder.options().baseDir(new File("src/test/resources")).inPlace(true).safe(SafeMode.UNSAFE).backend("html5").toFile(false).get());
    }



}