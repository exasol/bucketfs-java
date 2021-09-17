package com.exasol.bucketfs.jsonrpc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import jakarta.json.JsonStructure;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * This command creates a new bucket in BucketFS. Create new instances using the builder created by
 * {@link CommandFactory#makeCreateBucketCommand()}.
 */
// [impl->dsn~creating-new-bucket~1]
public class CreateBucketCommand extends AbstractJsonResponseCommand<Void> {

    private static final String JOB_NAME = "bucket_add";
    private final Request request;

    CreateBucketCommand(final JsonMapper jsonMapper, final Request request) {
        super(jsonMapper, JOB_NAME);
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

        private Request(final CreateBucketCommandBuilder builder) {
            this.bucketFsName = Objects.requireNonNull(builder.bucketFsName, "bucketFsName");
            this.bucketName = Objects.requireNonNull(builder.bucketName, "bucketName");
            this.isPublic = builder.isPublic;
            this.readPassword = base64Encode(builder.readPassword);
            this.writePassword = base64Encode(builder.writePassword);
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
    }

    /**
     * A builder for new {@link CreateBucketCommand}.
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

        private CreateBucketCommandBuilder(final JsonRpcCommandExecutor executor, final JsonMapper jsonMapper) {
            this.executor = executor;
            this.jsonMapper = jsonMapper;
        }

        /**
         * Set the name of the BucketFS in which to create the bucket, e.g. {@code "bfsdefault"}.
         *
         * @param bucketFsName name of the BucketFS in which to create the bucket
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder bucketFsName(final String bucketFsName) {
            this.bucketFsName = bucketFsName;
            return this;
        }

        /**
         * Set the name of bucket to create.
         *
         * @param bucketName name of bucket to create
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder bucketName(final String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        /**
         * Define if the new bucket should be public or not. Defaults to <code>false</code>.
         *
         * @param isPublic <code>true</code> if the bucket should be public, else <code>false</code> (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder isPublic(final boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        /**
         * Set the read password for the new bucket.
         *
         * @param readPassword read password or <code>null</code> for no read password (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return this;
        }

        /**
         * Set the write password for the new bucket.
         *
         * @param writePassword write password or <code>null</code> for no write password (default)
         * @return this instance for method chaining
         */
        public CreateBucketCommandBuilder writePassword(final String writePassword) {
            this.writePassword = writePassword;
            return this;
        }

        /**
         * Create a new bucket using the configured values.
         *
         * @throws NullPointerException in case mandatory fields are not defined
         */
        public void execute() {
            this.executor.execute(new CreateBucketCommand(this.jsonMapper, new Request(this)));
        }
    }
}
