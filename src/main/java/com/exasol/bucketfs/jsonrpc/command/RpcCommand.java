package com.exasol.bucketfs.jsonrpc.command;

public abstract class RpcCommand<R> {
    private final String jobName;

    protected RpcCommand(final String jobName) {
        this.jobName = jobName;
    }

    public final String getJobName() {
        return this.jobName;
    }

    public abstract Object getParameters();

    public abstract R processResult(String responsePayload);
}
