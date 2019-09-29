package org.uniqueck.asciidoctorj.exceltableconverter;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;

public class ExcelTableGeneratorExtension implements ExtensionRegistry {
    @Override
    public void register(Asciidoctor asciidoctor) {
        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();
        javaExtensionRegistry.blockMacro(ExcelTableGeneratorBlockMacroProcessor.class);
    }
}
