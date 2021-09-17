package com.exasol.bucketfs.jsonrpc;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * The base class for {@link RpcCommand} that expect a JSON response payload. This simplifies processing the result for
 * child classes.
 *
 * @param <R> the result type
 */
abstract class AbstractJsonResponseCommand<R> extends RpcCommand<R> {
    private static final String SUCCESS_RESULT_NAME = "OK";
    private static final int SUCCESS_RESULT_CODE = 0;
    protected final JsonMapper jsonMapper;

    /**
     * Create a new {@link AbstractJsonResponseCommand}.
     *
     * @param jsonMapper {@link JsonMapper} used for deserializing the response payload.
     * @param jobName    job name for the new command.
     */
    protected AbstractJsonResponseCommand(final JsonMapper jsonMapper, final String jobName) {
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
        if (hasErrorCode(result) || hasErrorDescription(result)) {
            throw new JsonRpcException(messageBuilder("E-BFSJ-18")
                    .message("RPC command {{command}} failed, received error result {{result}} from server.",
                            this.getClass().getName(), result)
                    .toString());
        }
    }

    private boolean hasErrorDescription(final JsonRpcResponse result) {
        return !SUCCESS_RESULT_NAME.equalsIgnoreCase(result.getName());
    }

    private boolean hasErrorCode(final JsonRpcResponse result) {
        return result.getCode() != SUCCESS_RESULT_CODE;
    }

    /**
     * Process the given response JSON payload and returns a result object.
     *
     * @param responsePayload parsed response payload.
     * @return result object
     */
    abstract R processResult(JsonStructure responsePayload);

    // Must be public for JSON mapping
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
