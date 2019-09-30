package org.uniqueck.asciidoctorj;

import lombok.AccessLevel;
import lombok.Getter;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.uniqueck.asciidoctorj.exceltableconverter.ExcelTableGeneratorBlockMacroProcessor;
import org.uniqueck.asciidoctorj.slidesconverter.SlidesImageGeneratorBlockMacroProcessor;

import java.io.File;

@Getter(AccessLevel.PROTECTED)
@Disabled
public abstract class AbstractAsciidoctorTestHelper {


    private Asciidoctor asciidoctor;

    protected Options createOptions() {
        return OptionsBuilder.options().baseDir(new File("src/test/resources")).inPlace(true).safe(SafeMode.UNSAFE).backend("html5").toFile(false).get();
    }

    @BeforeEach
    void setup() {
        asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().blockMacro(SlidesImageGeneratorBlockMacroProcessor.class);
        asciidoctor.javaExtensionRegistry().blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
    }

    protected String convert(String content) {
        return convert(content, createOptions());
    }

    protected String convert(String content, Options options) {
        return getAsciidoctor().convert(content, options);
    }

    protected String convertFile(String fileName) {
        return getAsciidoctor().convertFile(new File(fileName), createOptions());
    }

}
