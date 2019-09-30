package org.uniqueck.asciidoctorj.slidesconverter;

import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowFactory;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.uniqueck.asciidoctorj.exceltableconverter.exceptions.AsciidoctorOfficeRuntimeException;

import javax.imageio.ImageIO;
import javax.swing.*;
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
        if (slideFile.exists()) {
            XMLSlideShow xmlSlideShow = null;
            try {
                xmlSlideShow = new XMLSlideShow(new FileInputStream(slideFile));
                List<XSLFSlide> allSlides = xmlSlideShow.getSlides();
                if (!allSlides.isEmpty()) {
                    XSLFSlide slide = allSlides.get(0);
                    BufferedImage bufferedImage = new BufferedImage(xmlSlideShow.getPageSize().width, xmlSlideShow.getPageSize().height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = bufferedImage.createGraphics();
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    slide.draw(graphics);
                    // TODO get Iamgedir and save Image in Image Directory ImageIO.write(bufferedImage, "PNG", new File("test.png"));
                } else {
                    // FIXME
                }
            } catch (IOException e) {
                throw new AsciidoctorOfficeRuntimeException("Error on reading '" + target + "'",e);
            } finally {
                if (xmlSlideShow != null) {
                    try {
                        xmlSlideShow.close();
                    } catch (IOException e) {
                        throw new AsciidoctorOfficeRuntimeException("Error on closing slideshow '" + target + "'", e);
                    }
                }
            }
        } else {
            throw new AsciidoctorOfficeRuntimeException("ImpressFile '" + target + "' doesn't exist");
        }


        parseContent(structuralNode, contentLines);
        return null;
    }

    protected int getSlideIndex(Map<String, Object> attributes) {
        return (Integer)attributes.getOrDefault("slideIndex", (Integer)0);
    }

}
