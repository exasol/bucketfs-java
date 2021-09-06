package com.exasol.bucketfs.jsonrpc.command;

import com.exasol.bucketfs.jsonrpc.JsonMapper;
import com.exasol.bucketfs.jsonrpc.JsonRpcResponse;

import jakarta.json.JsonStructure;

abstract class JsonResponseCommand<R> extends RpcCommand<R> {
    private final JsonMapper jsonMapper;

    protected JsonResponseCommand(final JsonMapper jsonMapper, final String jobName) {
        super(jobName);
        this.jsonMapper = jsonMapper;
    }

    @Override
    public final R processResult(final String responsePayload) {
        final JsonRpcResponse payload = this.jsonMapper.deserialize(responsePayload, JsonRpcResponse.class);
        return this.processResult(payload.getOutput());
    }

    abstract R processResult(JsonStructure responsePayload);
}
