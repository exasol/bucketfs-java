package com.exasol.bucketfs.jsonrpc;

public class JsonRpcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonRpcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JsonRpcException(final String message) {
        super(message);
    }
}
