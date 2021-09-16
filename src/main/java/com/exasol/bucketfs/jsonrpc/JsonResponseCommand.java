package com.exasol.bucketfs.jsonrpc;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

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

    // Must be public for json mapping
    public static class JsonRpcResponse {
        @JsonbProperty("result_jobid")
        private String jobId;
        @JsonbProperty("result_code")
        private int code;
        @JsonbProperty("result_name")
        private String name;
        @JsonbProperty("result_desc")
        private String description;
        @JsonbProperty("result_revision")
        private int revision;
        @JsonbProperty("result_output")
        private JsonStructure output;

        public String getJobId() {
            return this.jobId;
        }

        public void setJobId(final String jobId) {
            this.jobId = jobId;
        }

        public int getCode() {
            return this.code;
        }

        public void setCode(final int code) {
            this.code = code;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }

        public int getRevision() {
            return this.revision;
        }

        public void setRevision(final int revision) {
            this.revision = revision;
        }

        public JsonStructure getOutput() {
            return this.output;
        }

        public void setOutput(final JsonStructure output) {
            this.output = output;
        }

        @Override
        public String toString() {
            return "Response [jobId=" + this.jobId + ", code=" + this.code + ", name=" + this.name + ", description="
                    + this.description + ", revision=" + this.revision + ", output=" + this.output + "]";
        }
    }
}
