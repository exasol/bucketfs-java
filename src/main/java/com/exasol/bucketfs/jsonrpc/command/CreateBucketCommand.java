package com.exasol.bucketfs.jsonrpc.command;

import java.nio.charset.StandardCharsets;
import java.util.*;

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

    static CreateBucketCommandBuilder builder(final JsonMapper jsonMapper) {
        return new CreateBucketCommandBuilder(jsonMapper);
    }

    public static class Request {
        // Mandatory values
        @JsonbProperty("bucketfs_name")
        private final String bucketFsName;
        @JsonbProperty("bucket_name")
        private final String bucketName;
        @JsonbProperty("public")
        private final boolean isPublic;

        // Optional values
        @JsonbProperty("read_password")
        private final String readPassword;
        @JsonbProperty("write_password")
        private final String writePassword;
        @JsonbProperty("additional_files")
        private final List<String> additionalFiles;

        private Request(final CreateBucketCommandBuilder builder) {
            this.bucketFsName = Objects.requireNonNull(builder.bucketFsName, "bucketFsName");
            this.bucketName = Objects.requireNonNull(builder.bucketName, "bucketName");
            this.isPublic = builder.isPublic;
            this.readPassword = base64Encode(builder.readPassword);
            this.writePassword = base64Encode(builder.writePassword);
            this.additionalFiles = builder.additionalFiles;
        }

        private static String base64Encode(final String value) {
            if (value == null) {
                return null;
            }
            return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
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

        public List<String> getAdditionalFiles() {
            return this.additionalFiles;
        }
    }

    public static final class CreateBucketCommandBuilder {
        private String bucketFsName = null;
        private String bucketName = null;
        private boolean isPublic = false;
        private String readPassword = null;
        private String writePassword = null;
        private List<String> additionalFiles = null;
        private final JsonMapper jsonMapper;

        private CreateBucketCommandBuilder(final JsonMapper jsonMapper) {
            this.jsonMapper = jsonMapper;
        }

        public CreateBucketCommandBuilder withBucketFsName(final String bucketFsName) {
            this.bucketFsName = bucketFsName;
            return this;
        }

        public CreateBucketCommandBuilder withBucketName(final String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public CreateBucketCommandBuilder withIsPublic(final boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public CreateBucketCommandBuilder withReadPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        public CreateBucketCommandBuilder withWritePassword(final String writePassword) {
            this.writePassword = writePassword;
            return this;
        }

        public CreateBucketCommandBuilder withAdditionalFiles(final List<String> additionalFiles) {
            this.additionalFiles = additionalFiles;
            return this;
        }

        public CreateBucketCommand build() {
            return new CreateBucketCommand(this.jsonMapper, new Request(this));
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
