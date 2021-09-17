package com.exasol.bucketfs.jsonrpc;

import java.util.Objects;

import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Executes {@link RpcCommand}s by converting them to a request, sending the request and processing the result.
 */
class JsonRpcCommandExecutor {
    private final JsonRpcClient client;
    private final JsonMapper jsonMapper;

    JsonRpcCommandExecutor(final JsonRpcClient client, final JsonMapper jsonMapper) {
        this.client = Objects.requireNonNull(client, "client");
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "jsonMapper");
    }

    <R> R execute(final RpcCommand<R> command) {
        final Object parameters = command.getParameters();
        final JsonObject jsonObjectParam = this.jsonMapper.toJsonObject(parameters);
        final String responseBody = this.client
                .sendRequest(new JsonRpcPayload("job_exec", command.getJobName(), jsonObjectParam));
        return command.processResult(responseBody);
    }

    // Must be public for JSON mapping
    public static class JsonRpcPayload {
        @JsonbProperty("method")
        private final String method;
        @JsonbProperty("job")
        private final String job;
        @JsonbProperty("params")
        private final Parameters parameters;

        private JsonRpcPayload(final String method, final String job, final JsonObject parameters) {
            this.method = method;
            this.job = job;
            this.parameters = new Parameters(parameters);
        }

        public String getMethod() {
            return this.method;
        }

        public String getJob() {
            return this.job;
        }

        public Parameters getParameters() {
            return this.parameters;
        }
    }

    // Must be public for JSON mapping
    public static class Parameters {
        @JsonbProperty("params")
        private final JsonObject params;

        private Parameters(final JsonObject params) {
            this.params = params;
        }

        public JsonObject getParams() {
            return this.params;
        }
    }
}
