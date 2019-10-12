package org.uniqueck.asciidoctorj.exceltableconverter;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Name;
import org.uniqueck.asciidoctorj.AbstractOfficeBlockMacroProcessor;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeFormatNotSupportedRuntimeException;

import java.io.File;
import java.util.List;
import java.util.Map;

@Name("excel")
public class ExcelTableGeneratorBlockMacroProcessor extends AbstractOfficeBlockMacroProcessor {


    @Override
    protected List<String> generateAsciiDocMarkup(StructuralNode parent, File sourceFile, Map<String, Object> attributes) {
        if (sourceFile.getName().toUpperCase().endsWith(".XLSX")) {
            return new XLSXTableConverter(sourceFile, getSheetName(attributes)).convert();
        } else if (sourceFile.getName().toUpperCase().endsWith(".XLS")) {
            return new XLSTableConverter(sourceFile, getSheetName(attributes)).convert();
        } else {
            throw new AsciidoctorOfficeFormatNotSupportedRuntimeException(sourceFile);
        }
    }

    protected String getSheetName(Map<String, Object> attributes) {
        return (String) attributes.get("sheetName");
    }


}
