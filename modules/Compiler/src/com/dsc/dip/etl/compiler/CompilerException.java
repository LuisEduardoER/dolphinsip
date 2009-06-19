package com.dsc.dip.etl.compiler;

/**
 * Exception provide infomation about.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class CompilerException extends Exception {

    /**
     * serial version uid for serialisation.
     */
    private static final long serialVersionUID = 3988134782684360576L;

    /**
     * Empty constructor.
     */
    public CompilerException() {
    }

    /**
     * Constructor with message and inner exception.
     * @param message
     *            - error message
     * @param cause
     *            - inner exception
     */
    public CompilerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with error message.
     * @param message
     *            - error message
     */
    public CompilerException(final String message) {
        super(message);
    }

    /**
     * Constructor with inner exception.
     * @param cause
     *            - inner exception
     */
    public CompilerException(final Throwable cause) {
        super(cause);
    }

}
