package org.uniqueck.asciidoctorj.slidesconverter;

import org.asciidoctor.Asciidoctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uniqueck.asciidoctorj.AbstractAsciidoctorTestHelper;

class SlidesImageGeneratorBlockMacroProcessorTest extends AbstractAsciidoctorTestHelper {

    @Test
    void allFine() {
        String content = convert("slide::simpleImpressWithSingleSlide.pptx[]");
    }


}
