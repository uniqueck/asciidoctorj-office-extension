package org.uniqueck.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import org.uniqueck.asciidoctorj.exceltableconverter.ExcelTableGeneratorBlockMacroProcessor;
import org.uniqueck.asciidoctorj.slidesconverter.SlidesImageGeneratorBlockMacroProcessor;

public class OfficeExtensionRegistry implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();
        javaExtensionRegistry.blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
        javaExtensionRegistry.blockMacro(SlidesImageGeneratorBlockMacroProcessor.class);
    }
}
