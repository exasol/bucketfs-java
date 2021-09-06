package com.exasol.bucketfs.jsonrpc.command;

import com.exasol.bucketfs.jsonrpc.JsonMapper;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

public class CreateBucketCommand extends JsonResponseCommand<CreateBucketCommand.Result> {

    private final Request request;

    protected CreateBucketCommand(final JsonMapper jsonMapper, final Request request) {
        super(jsonMapper, "bucket_add");
        this.request = request;
    }

    @Override
    public Request getParameters() {
        return this.request;
    }

    @Override
    Result processResult(final JsonStructure responsePayload) {
        return new Result(responsePayload);
    }

    public static class Request {
        @JsonbProperty("bucketfs_name")
        private final String bucketFsName;
        @JsonbProperty("bucket_name")
        private final String bucketName;
        @JsonbProperty("public")
        private final boolean isPublic;

        @JsonbProperty("read_password")
        private final String readPassword;
        @JsonbProperty("write_password")
        private final String writePassword;

        public Request(final String bucketFsName, final String bucketName, final boolean isPublic,
                final String readPassword,
                final String writePassword) {
            this.bucketFsName = bucketFsName;
            this.bucketName = bucketName;
            this.isPublic = isPublic;
            this.readPassword = readPassword;
            this.writePassword = writePassword;
        }

        public String getBucketFsName() {
            return this.bucketFsName;
        }

        public String getBucketName() {
            return this.bucketName;
        }

        public boolean isPublic() {
            return this.isPublic;
        }

        public String getReadPassword() {
            return this.readPassword;
        }

        public String getWritePassword() {
            return this.writePassword;
        }
    }

    public static class Result {

        private final JsonStructure response;

        private Result(final JsonStructure response) {
            this.response = response;
        }

        @Override
        public String toString() {
            return "Result [response=" + this.response + "]";
        }
    }
}
