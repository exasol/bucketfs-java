package com.exasol.bucketfs.jsonrpc;

import jakarta.json.JsonStructure;

abstract class JsonResponseCommand<R> extends RpcCommand<R> {
    protected final JsonMapper jsonMapper;

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

    abstract R processResult(JsonStructure responsePayload);
}
