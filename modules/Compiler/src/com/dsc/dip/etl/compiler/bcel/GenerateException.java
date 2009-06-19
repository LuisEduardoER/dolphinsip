package com.dsc.dip.etl.compiler.bcel;

/**
 * Error generate java byte-code by parsing syntax tree.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class GenerateException extends Exception {

    /**
     * serial version uid for serialisation.
     */
    private static final long serialVersionUID = -28825574197846722L;

    /**
     * Empty constructor.
     */
    public GenerateException() {
        super();
    }

    /**
     * Constructor with message and inner exception.
     * @param message
     *            - error message
     * @param cause
     *            - inner exception
     */
    public GenerateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with error message.
     * @param message
     *            - error message
     */
    public GenerateException(final String message) {
        super(message);
    }

    /**
     * Constructor with inner exception.
     * @param cause
     *            - inner exception
     */
    public GenerateException(final Throwable cause) {
        super(cause);
    }

}
