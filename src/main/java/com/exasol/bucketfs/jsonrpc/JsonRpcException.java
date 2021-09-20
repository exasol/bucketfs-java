package com.exasol.bucketfs.jsonrpc;

/**
 * {@link RuntimeException} that is thrown in case execution of an {@link RpcCommand} fails.
 */
public class JsonRpcException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance of a {@link JsonRpcException}.
     *
     * @param message error message
     * @param cause   exception that caused this one
     */
    public JsonRpcException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new instance of a {@link JsonRpcException}.
     *
     * @param message error message
     */
    public JsonRpcException(final String message) {
        super(message);
    }
}
