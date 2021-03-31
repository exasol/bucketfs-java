package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
class ClusterConfigurationBucketFactoryTest {
    @Test
    void testGetBucketInjectsAccessCredentials(
            @Mock final BucketFsSerivceConfigurationProvider serviceConfigurationProviderMock) {
        final String readPassword = "foo";
        final String writePassword = "bar";
        final String serviceName = "the_service";
        final String bucketName = "the_bucket";
        final String ipAddress = "192.168.1.1";
        final int port = 2850;
        final Map<Integer, Integer> portMappings = Map.of(port, port);
        final BucketConfiguration bucketConfiguration = BucketConfiguration.builder().name(bucketName)
                .readPassword(readPassword).writePassword(writePassword).build();
        final BucketFsServiceConfiguration serviceConfiguration = BucketFsServiceConfiguration.builder()
                .name(serviceName).httpPort(port).addBucketConfiguration(bucketConfiguration).build();
        when(serviceConfigurationProviderMock.getBucketFsServiceConfiguration(any())).thenReturn(serviceConfiguration);
        final BucketFactory factory = new ClusterConfigurationBucketFactory(null, ipAddress,
                serviceConfigurationProviderMock, portMappings);
        final Bucket bucket = factory.getBucket(serviceName, bucketName);
        assertAll(() -> assertThat(bucket.getReadPassword(), equalTo(readPassword)),
                () -> assertThat(bucket.getWritePassword(), equalTo(writePassword)));
    }
}