package org.uniqueck.asciidoctorj;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public abstract class AbstractOfficeBlockMacroProcessor extends BlockMacroProcessor {

    protected abstract List<String> generateAsciiDocMarkup(StructuralNode parent, File sourceFile, Map<String, Object> attributes);

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
        parseContent(parent, generateAsciiDocMarkup(parent, getTargetAsFile(parent, target), attributes));
        return null;
    }

    protected File getBuildDir(final StructuralNode structuralNode) {
        if (structuralNode == null) {
            return null;
        }
        final Map<Object, Object> globalOptions = structuralNode.getDocument().getOptions();

        String toDir = (String) globalOptions.get("to_dir");
        String destDir = (String) globalOptions.get("destination_dir");
        String buildDir = toDir != null ? toDir : destDir;
        return new File(buildDir);
    }

    protected String getAttribute(StructuralNode structuralNode, String attributeName, String defaultValue) {
        String value = (String) structuralNode.getAttribute(attributeName);

        if (value == null || value.trim().isEmpty()) {
            value = defaultValue;
        }

        return value;
    }

    public static File getTargetAsFile(final StructuralNode structuralNode, final String target) {
        if (structuralNode == null || target == null || target.isEmpty()) {
            return null;
        }
        final Map<Object, Object> globalOptions = structuralNode.getDocument().getOptions();

        final String docDir = (String) globalOptions.get("base_dir");
        final Path baseDirPath = Paths.get(docDir).normalize();

        final Path absoluteFilePath = baseDirPath.resolve(target).normalize();

        return absoluteFilePath.toFile();
    }

}
