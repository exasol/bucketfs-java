package com.exasol.bucketfs.jsonrpc.command;

import com.exasol.bucketfs.jsonrpc.JsonMapper;
import com.exasol.bucketfs.jsonrpc.command.CreateBucketCommand.CreateBucketCommandBuilder;

public class CommandFactory {
    private final JsonMapper jsonMapper;

    public CommandFactory(final JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public CreateBucketCommandBuilder makeCreateBucketCommand() {
        return CreateBucketCommand.builder(this.jsonMapper);
    }
}
