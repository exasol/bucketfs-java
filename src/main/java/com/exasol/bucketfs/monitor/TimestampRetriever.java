package com.exasol.bucketfs.monitor;

import java.time.Instant;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.BucketFsMonitor.StateRetriever;

/*
 * COPY from project exasol-testcontainers.
 * SHOULD be replaced by test dependency.
 */

public class TimestampRetriever implements StateRetriever {
    @Override
    public State getState() {
        return TimestampState.lowResolution(Instant.now());
    }
}
