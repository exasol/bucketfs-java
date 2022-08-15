package com.exasol.bucketfs.monitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.*;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;

class TimestampStateTest {

    private static final String STRING = "2022-05-20T09:20:44Z";
    private static final Instant INSTANT = Instant.parse(STRING);
    private static final TimestampState TESTEE = TimestampState.lowResolution(INSTANT.plusNanos(200));

    @Test
    void lowResolution() {
        assertThat(TESTEE.getTime(), equalTo(INSTANT));
        assertThat(TESTEE.getRepresentation(), equalTo("time " + STRING));
    }

    @Test
    void localDateTime() {
        final ZoneId zid = ZoneId.of("UTC");
        final LocalDateTime local = LocalDateTime.ofInstant(INSTANT, zid);
        final TimestampState actual = TimestampState.of(local, TimeZone.getTimeZone(zid));
        assertThat(actual.getTime(), equalTo(INSTANT));
    }

    @Test
    void accepts() {
        final Duration delta = Duration.ofNanos(1000);
        assertThat(TESTEE.accepts(TESTEE), is(true));
        assertThat(TESTEE.accepts(before(delta)), is(false));
        assertThat(TESTEE.accepts(after(delta)), is(true));
    }

    private State before(final Duration delta) {
        return TimestampState.lowResolution(INSTANT.minus(delta));
    }

    private State after(final Duration delta) {
        return TimestampState.lowResolution(INSTANT.plus(delta));
    }

}
