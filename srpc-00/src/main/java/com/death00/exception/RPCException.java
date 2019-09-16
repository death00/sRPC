package com.death00.exception;

/**
 * @author death00
 * @date 2019/9/16
 */
public class RPCException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RPCException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCException(String message) {
        super(message);
    }

    public RPCException(Throwable cause) {
        super(cause);
    }

}
