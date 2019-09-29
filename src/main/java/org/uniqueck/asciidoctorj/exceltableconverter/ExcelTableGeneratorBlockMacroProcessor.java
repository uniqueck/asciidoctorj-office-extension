package org.uniqueck.asciidoctorj.exceltableconverter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Name("excel")
public class ExcelTableGeneratorBlockMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode structuralNode, String target, Map<String, Object> attributes) {

        Map<String, Object> docAttributes = structuralNode.getDocument().getAttributes();

        File excelFile = new File((String) docAttributes.get("docdir"), target);
        if (excelFile.exists()) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(excelFile);
                String sheetName = (String) attributes.get("sheetName");
                if (sheetName == null || sheetName.trim().isEmpty()) {
                    throw new RuntimeException("Attribute 'sheetName' is missing");
                } else {
                    if (workbook.getSheet(sheetName) != null) {

                    } else {
                        throw new RuntimeException("Sheet with name '" +sheetName + "' couldn't found");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error on opening ExcelFile '" + target + "'",e);
            } finally {
                if (workbook != null ) {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Error on closing ExcelFile '" + target + "'",e);
                    }
                    workbook = null;
                }
            }

        } else {
            throw new RuntimeException("ExcelFile '" + target + "' doesn't exist");
        }


        return null;
    }
}
