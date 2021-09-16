package com.exasol.bucketfs.jsonrpc;

/**
 * A command with job name and parameters that can be executed against the RPC interface. Child classes can process the
 * response payload and return a custom model by implementing {@link #processResult(String)}.
 *
 * @param <R> the result type
 */
abstract class RpcCommand<R> {
    private final String jobName;

    /**
     * Creates a new command with the given job name, e.g. {@code "bucket_add"}.
     *
     * @param jobName the job name for the new command
     */
    protected RpcCommand(final String jobName) {
        this.jobName = jobName;
    }

    /**
     * The job name.
     *
     * @return the job name
     */
    final String getJobName() {
        return this.jobName;
    }

    /**
     * Parameters for the RPC request. The returned object must be serializable to Json.
     *
     * @return the parameters for the RPC request.
     */
    abstract Object getParameters();

    /**
     * Processes the given response payload (e.g. by parsing it as Json) and returns a result object.
     *
     * @param responsePayload the response payload.
     * @return result object
     */
    abstract R processResult(String responsePayload);
}
