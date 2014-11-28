package joptsimple.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static joptsimple.util.PathProperties.*;


public class PathPropertiesTest {

    @Test
    public void testReadableFile() throws IOException {
        Path path = Files.createTempFile("prefix", null);
        path.toFile().deleteOnExit();
        assertTrue(READABLE.accept(path));
        assertFalse(DIRECTORY_EXISTING.accept(path));
        assertTrue(FILE_EXISTING.accept(path));
        assertTrue(FILE_OVERWRITABLE.accept(path));
        assertTrue(WRITABLE.accept(path));
        assertFalse(NOT_EXISTING.accept(path));
    }

    @Test
    public void testNonExisting() throws IOException {
        Path path = Files.createTempFile("prefix", null);
        Files.deleteIfExists(path);
        assertFalse(READABLE.accept(path));
        assertFalse(DIRECTORY_EXISTING.accept(path));
        assertFalse(FILE_EXISTING.accept(path));
        assertTrue(NOT_EXISTING.accept(path));
        assertFalse(WRITABLE.accept(path));
    }

    @Test
    public void testDirectory() throws IOException {
        Path path = Files.createTempDirectory("prefix");
        path.toFile().deleteOnExit();
        assertTrue(READABLE.accept(path));
        assertTrue(DIRECTORY_EXISTING.accept(path));
        assertFalse(FILE_EXISTING.accept(path));
        assertFalse(FILE_OVERWRITABLE.accept(path));
        assertFalse(NOT_EXISTING.accept(path));
        assertTrue(WRITABLE.accept(path));
    }

}
