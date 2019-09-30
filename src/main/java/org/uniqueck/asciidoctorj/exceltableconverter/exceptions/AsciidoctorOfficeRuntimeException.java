package org.uniqueck.asciidoctorj.exceltableconverter.exceptions;

public class AsciidoctorOfficeRuntimeException extends RuntimeException {

    public AsciidoctorOfficeRuntimeException(String message, Throwable cause)  {
        super(message, cause);
    }

    public  AsciidoctorOfficeRuntimeException(String message) {
        super(message);
    }

}
