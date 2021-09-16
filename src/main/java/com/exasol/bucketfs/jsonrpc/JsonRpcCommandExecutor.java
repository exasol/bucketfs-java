package com.exasol.bucketfs.jsonrpc;

import jakarta.json.JsonObject;

/**
 * Executes {@link RpcCommand}s by converting them to a request, sending the request and processing the result.
 */
class JsonRpcCommandExecutor {
    private final JsonRpcClient client;
    private final JsonMapper jsonMapper;

    JsonRpcCommandExecutor(final JsonRpcClient client, final JsonMapper jsonMapper) {
        this.client = client;
        this.jsonMapper = jsonMapper;
    }

    <R> R execute(final RpcCommand<R> command) {
        final Object parameters = command.getParameters();
        final JsonObject jsonObjectParam = this.jsonMapper.toJsonObject(parameters);
        final String responseBody = this.client
                .sendRequest(new JsonRpcPayload("job_exec", command.getJobName(), jsonObjectParam));
        return command.processResult(responseBody);
    }
}
