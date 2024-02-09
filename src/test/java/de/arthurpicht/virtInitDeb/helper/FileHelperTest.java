package de.arthurpicht.virtInitDeb.helper;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {

    @Test
    public void isSubDirectoryTest1() {
        Path reference = Paths.get("/a", "b", "c", "d");
        Path subdirectory = Paths.get("/a", "b", "c", "d", "e");
        assertTrue(FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void isSubDirectoryTest2() {
        Path reference = Paths.get("/a", "b", "c");
        Path subdirectory = Paths.get("/a", "b", "c", "d", "e");
        assertTrue(FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void isSubDirectoryReferenceAsRoot() {
        Path reference = Paths.get("/");
        Path subdirectory = Paths.get("/a", "b", "c", "d", "e");
        assertTrue(FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void isSubDirectoryEquals() {
        Path reference = Paths.get("/a", "b", "c");
        Path subdirectory = Paths.get("/a", "b", "c");
        assertFalse(FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void isSubDirectoryNot() {
        Path reference = Paths.get("/a", "b", "c");
        Path subdirectory = Paths.get("/a", "b");
        assertFalse(FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void isSubDirectory_neg_referenceNotAbsolute() {
        Path reference = Paths.get("a", "b", "c", "d");
        Path subdirectory = Paths.get("/a", "b", "c", "d", "e");
        assertThrows(IllegalArgumentException.class, () -> FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void isSubDirectory_neg_subdirectoryNotAbsolute() {
        Path reference = Paths.get("/a", "b", "c", "d");
        Path subdirectory = Paths.get("a", "b", "c", "d", "e");
        assertThrows(IllegalArgumentException.class, () -> FileHelper.isSubdirectory(reference, subdirectory));
    }

    @Test
    public void getFirstSubdirectoryTest1() {
        Path fullPath = Paths.get("/a", "b", "c", "d", "e");
        Path reference = Paths.get("/a", "b");
        Path nearestSubDir = FileHelper.getFirstSubdirectory(fullPath, reference);
        assertEquals("/a/b/c", nearestSubDir.toString());
    }

    @Test
    public void getFirstSubdirectoryTest2() {
        Path fullPath = Paths.get("/a", "b", "c");
        Path reference = Paths.get("/a", "b");
        Path nearestSubDir = FileHelper.getFirstSubdirectory(fullPath, reference);
        assertEquals("/a/b/c", nearestSubDir.toString());
    }

    @Test
    public void getFirstSubdirectoryWithRootReference() {
        Path fullPath = Paths.get("/a", "b", "c");
        Path reference = Paths.get("/");
        Path nearestSubDir = FileHelper.getFirstSubdirectory(fullPath, reference);
        assertEquals("/a", nearestSubDir.toString());
    }

    @Test
    public void getFirstSubdirectory_neg_noSubDir() {
        Path fullPath = Paths.get("/a", "b", "c");
        Path reference = Paths.get("/e", "f");
        assertThrows(
                IllegalArgumentException.class,
                () -> FileHelper.getFirstSubdirectory(fullPath, reference));
    }

    @Test
    public void getFirstSubdirectory_neg_noAbsoluteDir() {
        Path fullPath = Paths.get("/a", "b", "c");
        Path reference = Paths.get("a", "b");
        assertThrows(
                IllegalArgumentException.class,
                () -> FileHelper.getFirstSubdirectory(fullPath, reference));
    }


}