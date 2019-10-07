package org.uniqueck.asciidoctorj.slidesconverter;

import org.apache.poi.openxml4j.exceptions.ODFNotOfficeXmlFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uniqueck.asciidoctorj.AbstractAsciidoctorTestHelper;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SlidesImageGeneratorBlockMacroProcessorTest extends AbstractAsciidoctorTestHelper {

    @DisplayName("slide::simpleImpressWithSingleSlide.pptx[] => first slide is extracted, and image title is set to 'Slide1'")
    @Test
    void noAttributesFirstSlideWithSlideNumberAsTitle() {
        String content = convert("slide::simpleImpressWithSingleSlide.pptx[]");
        assertTempDirectoryContainsDirectory("slides");
        assertTempDirectoryContainsFile("slides/simpleImpressWithSingleSlide.pptx-Slide1.png");
        assertTrue(content.contains("<div class=\"title\">Figure 1. Slide1</div>"));
    }

    @DisplayName("slide::simpleImpressWithSingleSlide.pptx[slideNumber=1] => slide with index 0 is extracted and image title is set to 'Slide1'")
    @Test
    void slideIndexSpecifiedSlideWithIndexExtractedAndWithSlideIndexAsTitle() {
        String content = convert("slide::simpleImpressWithSingleSlide.pptx[slideNumber=1]");
        assertTempDirectoryContainsDirectory("slides");
        assertTempDirectoryContainsFile("slides/simpleImpressWithSingleSlide.pptx-Slide1.png");
        assertTrue(content.contains("<div class=\"title\">Figure 1. Slide1</div>"));
    }

    @DisplayName("slide::simpleImpressWithSingleSlide.pptx[slideNumber=2] => slide with index 1 doesn't exist so exception is thrown")
    @Test
    void slideWithIndexIsSpecifiedButDoenstExistExceptionIsThrown() {
        AsciidoctorOfficeRuntimeException asciidoctorOfficeRuntimeException = assertThrows(AsciidoctorOfficeRuntimeException.class, () -> convert("slide::simpleImpressWithSingleSlide.pptx[slideNumber=2]"));
        assertEquals("Slide with number '2' doesn't exist", asciidoctorOfficeRuntimeException.getCause().getMessage());
        assertTempDirectoryDoNotContainsDirectory("slides");
    }

    @DisplayName("slide::notExist.pptx[] => input file doesn't exist so exception is thrown")
    @Test
    void inputFileDoesntExistExceptionIsThrown() {
        AsciidoctorOfficeRuntimeException asciidoctorOfficeRuntimeException = assertThrows(AsciidoctorOfficeRuntimeException.class, () -> convert("slide::notExist[]"));
        assertEquals("ImpressFile '"+new File(getBaseDir(), "notExist").getAbsolutePath()+"' doesn't exist", asciidoctorOfficeRuntimeException.getCause().getMessage());
        assertTempDirectoryDoNotContainsDirectory("slides");
    }


    @DisplayName("Open Document Format currently not supported")
    @Test
    void openDocumentFormatCurrentlyNotSupported() {
        ODFNotOfficeXmlFileException asciidoctorOfficeRuntimeException = assertThrows(ODFNotOfficeXmlFileException.class, () -> convert("slide::simpleImpressWithSingleSlide.odp[]"));
        assertTrue(asciidoctorOfficeRuntimeException.getMessage().contains("Formats like these (eg ODS, ODP) are not supported"));
    }


}
