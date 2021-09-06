package com.exasol.bucketfs.jsonrpc.command;

import com.exasol.bucketfs.jsonrpc.JsonMapper;
import com.exasol.bucketfs.jsonrpc.command.CreateBucketCommand.Request;
import com.exasol.bucketfs.jsonrpc.command.CreateBucketCommand.Result;

public class CommandFactory {
    private final JsonMapper jsonMapper;

    public CommandFactory(final JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public RpcCommand<Result> createBucket(final String bucketFsName, final String bucketName,
            final boolean isPublic, final String readPassword, final String writePassword) {
        return new CreateBucketCommand(this.jsonMapper,
                new Request(bucketFsName, bucketName, isPublic, readPassword, writePassword));
    }
}
