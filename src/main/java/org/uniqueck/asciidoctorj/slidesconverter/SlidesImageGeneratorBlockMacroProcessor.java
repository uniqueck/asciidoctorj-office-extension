package org.uniqueck.asciidoctorj.slidesconverter;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.asciidoctor.jruby.internal.AsciidoctorCoreException;
import org.jruby.RubySymbol;
import org.uniqueck.asciidoctorj.AbstractOfficeBlockMacroProcessor;
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
public class SlidesImageGeneratorBlockMacroProcessor extends AbstractOfficeBlockMacroProcessor {

    @Override
    protected List<String> generateAsciiDocMarkup(StructuralNode parent, File sourceFile, Map<String, Object> attributes) {
        List<String> contentLines = new ArrayList<>();

        int slideNumber = getSlideNumber(attributes);
        final String slidesDirectoryName = getAttribute(parent, "slides-dir-name", "slides");
        final File slidesDirectory = getSlidesDirectory(parent, slidesDirectoryName);

        File extractSlideImageFile = new SlideImageExtractor(slidesDirectory, sourceFile).extractSlideToImage(slideNumber);
        contentLines.add(".Slide" +  slideNumber);
        contentLines.add("image::" + slidesDirectoryName + "/" + extractSlideImageFile.getName() + "[]");

        return contentLines;
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

    protected int getSlideNumber(Map<String, Object> attributes) {
        return Integer.parseInt((String)attributes.getOrDefault("slideNumber", "1"));
    }

}
