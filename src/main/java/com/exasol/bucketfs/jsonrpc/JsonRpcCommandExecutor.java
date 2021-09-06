package com.exasol.bucketfs.jsonrpc;

import com.exasol.bucketfs.jsonrpc.command.RpcCommand;

import jakarta.json.JsonObject;

public class JsonRpcCommandExecutor {
    private final JsonRpcClient client;
    private final JsonMapper jsonMapper;

    public JsonRpcCommandExecutor(final JsonRpcClient client, final JsonMapper jsonMapper) {
        this.client = client;
        this.jsonMapper = jsonMapper;
    }

    public <R> R execute(final RpcCommand<R> command) {
        final Object parameters = command.getParameters();
        final JsonObject jsonObjectParam = this.jsonMapper.toJsonObject(parameters);
        final String responseBody = this.client
                .sendRequest(new JsonRpcPayload("job_exec", command.getJobName(), jsonObjectParam));
        return command.processResult(responseBody);
    }
}
