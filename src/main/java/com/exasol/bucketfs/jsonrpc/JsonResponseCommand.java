package com.exasol.bucketfs.jsonrpc;

import jakarta.json.JsonStructure;

/**
 * The base class for {@link RpcCommand} that expect a json response payload. This simplifies processing the result for
 * child classes.
 *
 * @param <R> the result type
 */
abstract class JsonResponseCommand<R> extends RpcCommand<R> {
    protected final JsonMapper jsonMapper;

    /**
     * Creates a new {@link JsonResponseCommand}.
     *
     * @param jsonMapper the {@link JsonMapper} used for deserializing the response payload.
     * @param jobName    the job name for the new command.
     */
    protected JsonResponseCommand(final JsonMapper jsonMapper, final String jobName) {
        super(jobName);
        this.jsonMapper = jsonMapper;
    }

    @Override
    final R processResult(final String responsePayload) {
        final JsonRpcResponse payload = this.jsonMapper.deserialize(responsePayload, JsonRpcResponse.class);
        verifySuccess(payload);
        return this.processResult(payload.getOutput());
    }

    private void verifySuccess(final JsonRpcResponse result) {
        if (result.getCode() != 0) {
            throw new JsonRpcException("Command returned non-zero result code: " + result);
        }
        if (!"OK".equalsIgnoreCase(result.getName())) {
            throw new JsonRpcException("Command returned non-OK result name: " + result);
        }
    }

    /**
     * Processes the given response json payload and returns a result object.
     *
     * @param responsePayload the parsed response payload.
     * @return result object
     */
    abstract R processResult(JsonStructure responsePayload);
}
