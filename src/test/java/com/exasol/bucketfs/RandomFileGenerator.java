package com.exasol.bucketfs;

import java.io.*;
import java.nio.file.Path;
import java.util.Random;

/**
 * random file generator
 */
public final class RandomFileGenerator {
    /**
     * @param file file
     * @param sizeInKiB size in kb
     * @throws FileNotFoundException FileNotFoundException
     * @throws IOException IOException
     */
    public void createRandomFile(final Path file, final int sizeInKiB) throws FileNotFoundException, IOException {
        try (final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file.toFile()))) {
            writeRandomBytesToStream(stream, sizeInKiB);
        }
    }

    /**
     * @param stream stream
     * @param sizeInKiB size in kb
     * @throws IOException io exception
     */
    public void writeRandomBytesToStream(final OutputStream stream, final int sizeInKiB) throws IOException {
        final Random random = new Random();
        for (int i = 0; i < sizeInKiB; ++i) {
            final byte bytes[] = new byte[1024];
            random.nextBytes(bytes);
            stream.write(bytes);
        }
    }
}