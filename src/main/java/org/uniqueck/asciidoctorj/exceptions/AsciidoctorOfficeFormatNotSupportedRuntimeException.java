package org.uniqueck.asciidoctorj.exceptions;

import java.io.File;

public class AsciidoctorOfficeFormatNotSupportedRuntimeException extends AsciidoctorOfficeRuntimeException {

    public AsciidoctorOfficeFormatNotSupportedRuntimeException(File file) {
        super(String.format("Format for file '%s' currently not supported", file.getName()));
    }
}
