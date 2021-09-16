package com.exasol.bucketfs.jsonrpc;

/**
 * {@link RuntimeException} that is thrown in case execution of an {@link RpcCommand} fails.
 */
public class JsonRpcException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonRpcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JsonRpcException(final String message) {
        super(message);
    }
}
