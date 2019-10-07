package org.uniqueck.asciidoctorj.slidesconverter;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.asciidoctor.jruby.internal.AsciidoctorCoreException;
import org.jruby.RubySymbol;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("slide")
public class SlidesImageGeneratorBlockMacroProcessor extends BlockMacroProcessor {

    @Override
    public Object process(StructuralNode structuralNode, String target, Map<String, Object> attributes) {

        List<String> contentLines = new ArrayList<>();
        Map<String, Object> docAttributes = structuralNode.getDocument().getAttributes();

        File slideFile = new File((String) docAttributes.get("docdir"), target);

        int slideNumber = getSlideNumber(attributes);
        final String slidesDirectoryName = getAttribute(structuralNode, "slides-dir-name", "slides");
        final File slidesDirectory = getSlidesDirectory(structuralNode, slidesDirectoryName);

        File extractSlideImageFile = new SlideImageExtractor(slidesDirectory, slideFile).extractSlideToImage(slideNumber);
        contentLines.add(".Slide" +  slideNumber);
        contentLines.add("image::" + slidesDirectoryName + "/" + extractSlideImageFile.getName() + "[]");


        parseContent(structuralNode, contentLines);
        return null;
    }

    private File getSlidesDirectory(StructuralNode structuralNode, String slidesDirectoryName) {
        final String imagesDirName = getAttribute(structuralNode, "imagesdir", "");
        File slidesDirectory;
        if (new File(imagesDirName).isAbsolute()) {
            slidesDirectory = new File(imagesDirName + '/' + slidesDirectoryName);
        } else {
            final File buildDir = getBuildDir(structuralNode);
            slidesDirectory = new File(buildDir, imagesDirName + '/' + slidesDirectoryName);
        }
        return slidesDirectory;
    }

    private File getBuildDir(StructuralNode structuralNode) {
        Map<Object, Object> globalOptions = structuralNode.getDocument().getOptions();

        String toDir = (String) globalOptions.get("to_dir");
        String destDir = (String) globalOptions.get("destination_dir");
        String buildDir = toDir != null ? toDir : destDir;
        return new File(buildDir);
    }

    protected int getSlideNumber(Map<String, Object> attributes) {
        return Integer.parseInt((String)attributes.getOrDefault("slideNumber", "1"));
    }

    private String getAttribute(StructuralNode structuralNode, String attributeName, String defaultValue) {
        String value = (String) structuralNode.getAttribute(attributeName);

        if (value == null || value.trim().isEmpty()) {
            value = defaultValue;
        }

        return value;
    }

}
