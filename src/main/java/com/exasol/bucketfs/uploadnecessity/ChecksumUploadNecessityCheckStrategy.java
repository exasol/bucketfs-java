package com.exasol.bucketfs.uploadnecessity;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;
import static com.exasol.bucketfs.uploadnecessity.ByteArrayToHexConverter.toHex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.ReadOnlyBucket;
import com.exasol.errorreporting.ExaError;

/**
 * This {@link UploadNecessityCheckStrategy} compares the checksum of the local file and the file on BucketFS and only
 * uploads the file if the checksums differ or if the file does not exist in BucketFS. For performance reasons this
 * strategy always uploads files with a size less or equal 1 MB.
 */
public class ChecksumUploadNecessityCheckStrategy implements UploadNecessityCheckStrategy {
    private static final String UDF_SCHEMA = "BUCKET_FS_JAVA_HELPER";
    private static final String UDF_NAME = "BUCKET_FS_CHECKSUM";
    private static final String UDF_FULL_NAME = UDF_SCHEMA + "." + UDF_NAME;
    private static final int ONE_MEGABYTE = 1000000;
    private final Connection sqlConnection;

    /**
     * Create a new instance of {@link ChecksumUploadNecessityCheckStrategy}.
     * <p>
     * This strategy uses a User Defined Function (UDF) to create the checksum of the file in BucketFS. Therefore it
     * requires a database connection.
     * </p>
     * 
     * @param sqlConnection SQL connection to the Exasol database
     */
    public ChecksumUploadNecessityCheckStrategy(final Connection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    @Override
    // [impl->dsn~conditional-upload-by-existence~1]
    // [impl->dsn~conditional-upload-by-size~1]
    // [impl->dsn~conditional-upload-by-checksum~1]
    public boolean isUploadNecessary(final Path file, final String fullFileNameInBucketFs, final ReadOnlyBucket bucket)
            throws BucketAccessException {
        try {
            final String[] parts = fullFileNameInBucketFs.split(PATH_SEPARATOR);
            final String fileName = parts[parts.length - 1];
            final List<String> filesInBucketDirectory = bucket.listContents(getDirectory(parts));
            if (Files.size(file) > ONE_MEGABYTE && filesInBucketDirectory.contains(fileName)) {
                return !localSha512Checksum(file).equals(getSha512Checksum(fullFileNameInBucketFs, bucket));
            } else {
                return true;
            }
        } catch (final BucketAccessException | NoSuchAlgorithmException | IOException exception) {
            throw new BucketAccessException(ExaError.messageBuilder("E-BFSJ-17")
                    .message("Failed to check if we need to upload {{file}}.", fullFileNameInBucketFs).toString(),
                    exception);
        }
    }

    private String getDirectory(final String[] parts) {
        final String directory = Arrays.stream(parts).limit((long) parts.length - 1)
                .collect(Collectors.joining(PATH_SEPARATOR));
        if (directory.startsWith(PATH_SEPARATOR)) {
            return directory.substring(1);
        } else {
            return directory;
        }
    }

    private String localSha512Checksum(final Path localPath) throws NoSuchAlgorithmException, IOException {
        final MessageDigest checksumBuilder = MessageDigest.getInstance("SHA-512");
        try (final InputStream inputStream = Files.newInputStream(localPath);
                final DigestInputStream checksumBuildingStream = new DigestInputStream(inputStream, checksumBuilder)) {
            final byte[] buffer = new byte[1000];
            while (checksumBuildingStream.read(buffer) != -1) {
                // nothing to do. Just read to run through the stream.
            }
        }
        return toHex(checksumBuilder.digest());
    }

    /**
     * Get the SHA-512-checksum of a file in BucketFS.
     *
     * @param fileInBucketFs path to a file in bucketFs
     * @param bucket         bucket the file is stored in
     * @return sha 512 checksum
     * @throws BucketAccessException if checksum calculation failed
     */
    public String getSha512Checksum(final String fileInBucketFs, final ReadOnlyBucket bucket)
            throws BucketAccessException {
        installChecksumUdf();
        try (final PreparedStatement statement = this.sqlConnection
                .prepareStatement("SELECT " + UDF_FULL_NAME + "(?)")) {
            final String pathInUdf = bucket.getPathInUdf(fileInBucketFs);
            statement.setString(1, pathInUdf);
            try (final ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getString(1);
            }
        } catch (final SQLException exception) {
            throw new BucketAccessException(ExaError.messageBuilder("F-BFSJ-15").message(
                    "Failed to determine checksum of file {{file}} in BucketFS using UDF " + UDF_FULL_NAME + ".",
                    fileInBucketFs).toString(), exception);
        } finally {
            uninstallChecksumUdf();
        }
    }

    private void installChecksumUdf() throws BucketAccessException {
        try (final Statement statement = this.sqlConnection.createStatement()) {
            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + UDF_SCHEMA + ";");
            statement.executeUpdate(getChecksumUdfStatement());
        } catch (final SQLException exception) {
            throw new BucketAccessException(ExaError.messageBuilder("E-BFSJ-14").message(
                    "Failed to install sha-512 checksum UDF. This UDF is required by bucketfs-java for building the checksum of files in BucketFS.")
                    .toString(), exception);
        }
    }

    private void uninstallChecksumUdf() throws BucketAccessException {
        try (final Statement statement = this.sqlConnection.createStatement()) {
            statement.executeUpdate("DROP SCRIPT  " + UDF_FULL_NAME + ";");
            statement.executeUpdate("DROP SCHEMA  " + UDF_SCHEMA + ";");
        } catch (final SQLException exception) {
            throw new BucketAccessException(ExaError.messageBuilder("E-BFSJ-16")
                    .message("Failed to uninstall sha-512 checksum UDF.").toString(), exception);
        }
    }

    private String getChecksumUdfStatement() {
        try {
            final byte[] pythonScriptAsBytes = Objects
                    .requireNonNull(getClass().getClassLoader().getResourceAsStream("checksumUdf.py")).readAllBytes();
            final String pythonScript = new String(pythonScriptAsBytes, StandardCharsets.UTF_8);
            return "CREATE OR REPLACE PYTHON3 SCALAR SCRIPT " + UDF_FULL_NAME
                    + "(my_path VARCHAR(2000)) RETURNS VARCHAR(256) AS\n" + pythonScript + "\n/";
        } catch (final IOException | NullPointerException exception) {
            throw new IllegalStateException(ExaError.messageBuilder("F-BFSJ-13")
                    .message("Failed to get Python UDF from resources.").ticketMitigation().toString(), exception);
        }
    }
}
