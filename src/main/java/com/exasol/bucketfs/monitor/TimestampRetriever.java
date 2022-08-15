package com.exasol.bucketfs.monitor;

import java.time.Instant;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.BucketFsMonitor.StateRetriever;

/**
 * COPY from project exasol-testcontainers.
 * <p>
 * SHOULD be replaced by test dependency.
 * </p>
 *
 * Retrieve the initial {@link TimestampState}, i.e. the current instant in time. This enables to reject events that
 * happened to an earlier point in time.
 */
public class TimestampRetriever implements StateRetriever {
    @Override
    public State getState() {
        return TimestampState.lowResolution(Instant.now());
    }
}
