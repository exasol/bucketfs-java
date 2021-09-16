package com.exasol.bucketfs.jsonrpc;

import java.nio.charset.StandardCharsets;
import java.util.*;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * This command creates a new bucket in BucketFS. Create new instances using the builder created by
 * {@link CommandFactory#makeCreateBucketCommand()}.
 */
// [impl->dsn~creating-new-bucket~1]
public class CreateBucketCommand extends JsonResponseCommand<Void> {

    private final Request request;

    CreateBucketCommand(final JsonMapper jsonMapper, final Request request) {
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

    // Must be public to allow json mapping. Not accessible for the user.
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

    /**
     * A builder for new {@link CreateBucketCommand}. Create a new instance using
     * {@link CommandFactory#makeCreateBucketCommand()}.
     * <p>
     * Mandatory fields are
     * <ul>
     * <li>{@link #bucketFsName}</li>
     * <li>{@link #bucketName}</li>
     * </ul>
     */
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

        /**
         * Sets the name of the BucketFS in which to create the bucket, e.g. {@code "bfsdefault"}.
         *
         * @param bucketFsName the name of the BucketFS in which to create the bucket
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder bucketFsName(final String bucketFsName) {
            this.bucketFsName = bucketFsName;
            return this;
        }

        /**
         * Sets the name of bucket to create.
         *
         * @param bucketName the name of bucket to create
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder bucketName(final String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        /**
         * Defines if the new bucket should be public or not. Defaults to <code>false</code>.
         *
         * @param isPublic <code>true</code> if the bucket should be public, else <code>false</code> (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder isPublic(final boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        /**
         * Sets the read password for the new bucket.
         *
         * @param readPassword the read password or <code>null</code> for no read password (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        /**
         * Sets the write password for the new bucket.
         *
         * @param writePassword the write password or <code>null</code> for no write password (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder writePassword(final String writePassword) {
            this.writePassword = writePassword;
            return this;
        }

        /**
         * Sets a list of additional files.
         *
         * @param additionalFiles the additional files for the new bucket or <code>null</code> for additional files
         *                        (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder additionalFiles(final List<String> additionalFiles) {
            this.additionalFiles = additionalFiles;
            return this;
        }

        /**
         * Creates a new bucket using the configured values.
         *
         * @throws NullPointerException in case mandatory fields are not defined
         */
        public void execute() {
            this.executor.execute(new CreateBucketCommand(this.jsonMapper, new Request(this)));
        }
    }
}
