package com.exasol.bucketfs;

import static com.exasol.errorreporting.ExaError.messageBuilder;
import static java.net.HttpURLConnection.*;

import java.net.URI;

public class HttpRequestStatus {

    /**
     * Evaluates the request status.
     *
     * @param uri        uri
     * @param operation  operation
     * @param statusCode statusCode
     * @throws BucketAccessException BucketAccessException
     */
    public static void evaluate(final URI uri, final BucketOperation operation, final int statusCode)
            throws BucketAccessException {
        switch (statusCode) {
        case HTTP_OK:
            return;
        case HTTP_NOT_FOUND:
            throw new BucketAccessException(messageBuilder("E-BFSJ-2")
                    .message("File or directory not found trying to {{operation|uq}} {{URI}}.", operation, uri)
                    .toString());
        case HTTP_FORBIDDEN:
            throw new BucketAccessException(messageBuilder("E-BFSJ-3")
                    .message("Access denied trying to {{operation|uq}} {{URI}}.", operation, uri).toString());
        default:
            throw new BucketAccessException(messageBuilder("E-BFSJ-1")
                    .message("Unable to perform {{operation|uq}} {{URI}}. HTTP status {{status}}.", operation, uri,
                            statusCode)
                    .toString());
        }
    }
}
