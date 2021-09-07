package com.exasol.bucketfs.jsonrpc;

abstract class RpcCommand<R> {
    private final String jobName;

    protected RpcCommand(final String jobName) {
        this.jobName = jobName;
    }

    final String getJobName() {
        return this.jobName;
    }

    abstract Object getParameters();

    abstract R processResult(String responsePayload);
}
