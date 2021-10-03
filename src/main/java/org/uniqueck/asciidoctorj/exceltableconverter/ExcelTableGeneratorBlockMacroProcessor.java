package org.uniqueck.asciidoctorj.exceltableconverter;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Name;
import org.uniqueck.asciidoctorj.AbstractOfficeBlockMacroProcessor;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeFormatNotSupportedRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("excel")
public class ExcelTableGeneratorBlockMacroProcessor extends AbstractOfficeBlockMacroProcessor {

    @Override
    protected List<String> generateAsciiDocMarkup(final StructuralNode parent, final File sourceFile, final Map<String, Object> attributes) {
        if (sourceFile == null) {
            return new ArrayList<>();
        }

        if (sourceFile.getName().toLowerCase().endsWith(".xlsx")) {
            return new XLSXTableConverter(sourceFile, attributes).convert();
        } else if (sourceFile.getName().toLowerCase().endsWith(".xls")) {
            return new XLSTableConverter(sourceFile, attributes).convert();
        } else {
            throw new AsciidoctorOfficeFormatNotSupportedRuntimeException(sourceFile);
        }
    }

}
