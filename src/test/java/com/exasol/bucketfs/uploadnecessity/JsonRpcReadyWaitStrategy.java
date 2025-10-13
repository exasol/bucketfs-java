package com.exasol.bucketfs.uploadnecessity;

/**
 * Wait strategy that ensures the JSON RPC is available before it is used.
 */
public class JsonRpcReadyWaitStrategy {
    boolean isReady = false;

    /**
     * Create a new instance of the {@link JsonRpcReadyWaitStrategy}.
     */
    public JsonRpcReadyWaitStrategy() {
        // Intentionally empty.
    }

    // This is a workaround until a proper readiness check for the XML RPC is implemented.
    @SuppressWarnings("sonar:S2925")
    public void waitUntilXmlRpcReady() {
        if(!isReady) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
            isReady = true;
        }
    }
}
