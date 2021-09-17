package com.exasol.bucketfs.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CommandFactoryTest {

    @Test
    void testCreatingCommandFactoryWithoutAuthenticatorFails() {
        asserBuildingFails(CommandFactory.builder(), NullPointerException.class, "authenticator");
    }

    @Test
    void testCreatingCommandFactoryWithoutUriFails() {
        asserBuildingFails(CommandFactory.builder().bearerTokenAuthentication("token"), NullPointerException.class,
                "serviceUri");
    }

    @Test
    void testCreatingCommandFactoryWithInvalidServerUrlFails() {
        final var builder = CommandFactory.builder();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.serverUrl("invalid url"));
        assertThat(exception.getMessage(), equalTo("E-BFSJ-19: Error parsing server URL 'invalid url'"));
    }

    private void asserBuildingFails(final CommandFactory.Builder builder,
            final Class<? extends Throwable> expectedException, final String expectedMessage) {
        final Throwable exception = assertThrows(expectedException, builder::build);
        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }
}
