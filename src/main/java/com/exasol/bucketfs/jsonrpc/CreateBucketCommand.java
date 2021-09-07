package com.exasol.bucketfs.jsonrpc;

import java.nio.charset.StandardCharsets;
import java.util.*;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

public class CreateBucketCommand extends JsonResponseCommand<Void> {

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
    Void processResult(final JsonStructure responsePayload) {
        return null;
    }

    static CreateBucketCommandBuilder builder(final JsonRpcCommandExecutor executor, final JsonMapper jsonMapper) {
        return new CreateBucketCommandBuilder(executor, jsonMapper);
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
        private final JsonMapper jsonMapper;
        private final JsonRpcCommandExecutor executor;

        private String bucketFsName = null;
        private String bucketName = null;
        private boolean isPublic = false;
        private String readPassword = null;
        private String writePassword = null;
        private List<String> additionalFiles = null;

        private CreateBucketCommandBuilder(final JsonRpcCommandExecutor executor, final JsonMapper jsonMapper) {
            this.executor = executor;
            this.jsonMapper = jsonMapper;
        }

        public CreateBucketCommandBuilder bucketFsName(final String bucketFsName) {
            this.bucketFsName = bucketFsName;
            return this;
        }

        public CreateBucketCommandBuilder bucketName(final String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public CreateBucketCommandBuilder isPublic(final boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public CreateBucketCommandBuilder readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        public CreateBucketCommandBuilder writePassword(final String writePassword) {
            this.writePassword = writePassword;
            return this;
        }

        public CreateBucketCommandBuilder additionalFiles(final List<String> additionalFiles) {
            this.additionalFiles = additionalFiles;
            return this;
        }

        public void execute() {
            this.executor.execute(new CreateBucketCommand(this.jsonMapper, new Request(this)));
        }
    }
}
