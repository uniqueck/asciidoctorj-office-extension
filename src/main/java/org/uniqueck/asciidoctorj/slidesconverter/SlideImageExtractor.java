package org.uniqueck.asciidoctorj.slidesconverter;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.uniqueck.asciidoctorj.exceptions.AsciidoctorOfficeRuntimeException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Getter(AccessLevel.PACKAGE)
public class SlideImageExtractor {


    private final File slideImageDirectory;
    private final File impressSourceFile;

    SlideImageExtractor(File slideImageDirectory, File impressSourceFile) {
        this.slideImageDirectory = slideImageDirectory;
        this.impressSourceFile = impressSourceFile;
    }

    public File extractSlideToImage(int slideNumber) {
        File targetFile = null;
        if (getImpressSourceFile().exists()) {
            XMLSlideShow xmlSlideShow = null;
            try {
                xmlSlideShow = new XMLSlideShow(new FileInputStream(getImpressSourceFile()));
                List<XSLFSlide> allSlides = xmlSlideShow.getSlides();
                if (slideNumber > allSlides.size()) {
                    throw new AsciidoctorOfficeRuntimeException("Slide with number '" + slideNumber + "' doesn't exist");
                }
                XSLFSlide slide = allSlides.get(slideNumber - 1);
                BufferedImage bufferedImage = new BufferedImage(xmlSlideShow.getPageSize().width, xmlSlideShow.getPageSize().height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = bufferedImage.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                slide.draw(graphics);

                targetFile = getTargetFile(slide);
                ImageIO.write(bufferedImage, "PNG", targetFile);

            } catch (IOException e) {
                throw new AsciidoctorOfficeRuntimeException("Error on reading '" + getImpressSourceFile().getPath() + "'",e);
            } finally {
                if (xmlSlideShow != null) {
                    try {
                        xmlSlideShow.close();
                    } catch (IOException e) {
                        throw new AsciidoctorOfficeRuntimeException("Error on closing slideshow '" + getImpressSourceFile().getPath() + "'", e);
                    }
                }
            }
        } else {
            throw new AsciidoctorOfficeRuntimeException("ImpressFile '" + getImpressSourceFile().getPath() + "' doesn't exist");
        }
        return targetFile;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getTargetFile(XSLFSlide slide) {
        File targetFile;
        final String slideName = slide.getSlideName() != null && !slide.getSlideName().trim().isEmpty() ? slide.getSlideName() : "" + slide.getSlideNumber();
        final String slideFileName = String.format("%s-%s.png", getImpressSourceFile().getName(), slideName);
        if (!getSlideImageDirectory().exists()) {
            getSlideImageDirectory().mkdirs();
        }
        targetFile = new File(getSlideImageDirectory(), slideFileName);
        return targetFile;
    }

}
