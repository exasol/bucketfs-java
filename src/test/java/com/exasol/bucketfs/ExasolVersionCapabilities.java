package com.exasol.bucketfs;

import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;

/**
 * This class defines things different Exasol versions can do or require.
 */
public class ExasolVersionCapabilities {
    private final ExasolContainer<? extends ExasolContainer<?>> container;

    /**
     * Create a new instance of the {@link ExasolVersionCapabilities} from a test container.
     *
     * @param container test container from which to retrieve the image details
     */
    private ExasolVersionCapabilities(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
    }

    public static ExasolVersionCapabilities of(final ExasolContainer<? extends ExasolContainer<?>> container) {
        return new ExasolVersionCapabilities(container);
    }

    /**
     * @return {@code true} if the Exasol installation in the test container requires using TLS to access BucketFS by
     * default.
     */
    public boolean requiresTlsForBucketFs() {
        final ExasolDockerImageReference version = container.getDockerImageReference();
        return ((version.getMajor() == 8) && (version.getMinor() > 28))
                || version.getMajor() > 8;
    }

    /**
     * @return {@code true} if bucket passwords need to be Base64 encoded on the client-side when using the RPC API.
     */
    public boolean requiresBase64EncodingBucketFsPasswordsOnClientSide() {
        final ExasolDockerImageReference version = container.getDockerImageReference();
        return version.getMajor() < 8;
    }
}
