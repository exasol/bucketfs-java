package com.exasol.bucketfs.jsonrpc;

import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbProperty;

public class JsonRpcPayload {

    @JsonbProperty("method")
    private final String method;
    @JsonbProperty("job")
    private final String job;
    @JsonbProperty("params")
    private final Parameters parameters;

    public JsonRpcPayload(final String method, final String job, final JsonObject parameters) {
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

    public class Parameters {
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
