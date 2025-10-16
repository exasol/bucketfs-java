package com.exasol.bucketfs;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * Exception for problems when accessing buckets or their contents in Exasol's BucketFS.
 *
 * @serial exclude
 */
public class BucketAccessException extends Exception {
    private static final long serialVersionUID = -1002852289020779835L;
    private final URI uri;
    private final int statusCode;

    /**
     * @param uri       URI of the request
     * @param operation operation initially requested {@link BucketOperation}
     * @return new instance of {@link BucketAccessException} indicating an interruption during download
     */
    public static BucketAccessException downloadInterruptedException(final URI uri, final BucketOperation operation) {
        return new BucketAccessException(messageBuilder("E-BFSJ-4")
                .message("Interrupted trying to {{operation|uq}} {{URI}}.", operation, uri).toString());
    }

    /**
     * @param uri        URI of the request
     * @param operation  operation initially requested {@link BucketOperation}
     * @param exception cause of the current exception
     * @return new instance of {@link BucketAccessException} indicating an IO failure during download
     */
    public static BucketAccessException downloadIoException(final URI uri, final BucketOperation operation,
            final IOException exception) {
        return new BucketAccessException(messageBuilder("E-BFSJ-5")
                .message("I/O error trying to {{operation|uq}} {{URI}}", operation, uri).toString(), exception);
    }

    /**
     * Create a new instance of a {@link BucketAccessException}.
     *
     * @param message error message
     * @param uri     URI that was attempted to access
     * @param cause   exception that caused this one
     */
    public BucketAccessException(final String message, final URI uri, final Throwable cause) {
        super(message + " URI: " + uri, cause);
        this.uri = uri;
        this.statusCode = 0;
    }

    /**
     * Create a new instance of a {@link BucketAccessException}.
     *
     * @param message    error message
     * @param statusCode HTTP response code
     * @param uri        URI that was attempted to access
     */
    public BucketAccessException(final String message, final int statusCode, final URI uri) {
        super(message + "URI: " + uri + " (Status " + statusCode + ")");
        this.statusCode = statusCode;
        this.uri = uri;
    }

    /**
     * Create a new instance of a {@link BucketAccessException}.
     *
     * @param message error message
     * @param cause   exception that caused this one
     */
    public BucketAccessException(final String message, final Throwable cause) {
        super(message, cause);
        this.uri = null;
        this.statusCode = 0;
    }

    /**
     * Create a new instance of a {@link BucketAccessException}.
     *
     * @param message error message
     */
    public BucketAccessException(final String message) {
        super(message);
        this.uri = null;
        this.statusCode = 0;
    }

    /**
     * @return URI that was tried to access
     */
    public URI getUri() {
        return this.uri;
    }

    /**
     * @return HTTP status code
     */
    public int getStatusCode() {
        return this.statusCode;
    }
}