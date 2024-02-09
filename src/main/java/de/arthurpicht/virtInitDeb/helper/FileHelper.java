package de.arthurpicht.virtInitDeb.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    public static Path getHomeDir() {
        return Paths.get(System.getProperty("user.home"));
    }

    public static boolean isSubdirectory(Path reference, Path subDirectory) {
        if (!reference.isAbsolute())
            throw new IllegalArgumentException("Path specified as reference is not absolute: [" + reference + "]");
        if (!subDirectory.isAbsolute())
            throw new IllegalArgumentException("Path specified as subDirectory is not absolute: [" + subDirectory + "]");
        return subDirectory.toString().startsWith(reference.toString())
               && !reference.toString().equals(subDirectory.toString());
    }

    /**
     * Finds the first subdirectory for specified reference path for a given full path.
     * Example: fullPath = /a/b/c/d/e, reference = /a/b/c, will result in /a/b/c/d.
     * No access to filesystem is made. Method works with path objects that do not correspond
     * with existing paths. Path specifications must be done as absolute paths.
     *
     * @param fullPath absolute path
     * @param referencePath absolute path, subdirectory of fullPath
     * @return next subdirectory of referencePath on fullPath
     */
    public static Path getFirstSubdirectory(Path fullPath, Path referencePath) {
        if (!fullPath.isAbsolute())
            throw new IllegalArgumentException("Path specified as full path is not absolute: [" + fullPath + "]");
        if (!referencePath.isAbsolute())
            throw new IllegalArgumentException("Path specified as reference path is not absolute: [" + referencePath + "]");
        if (!fullPath.toString().startsWith(referencePath.toString()) || fullPath.toString().equals(referencePath.toString()))
            throw new IllegalArgumentException("Reference path is no subdirectory of full path.");

        Path relative = referencePath.relativize(fullPath);
        Path firstElement = relative.getName(0);
        return referencePath.resolve(firstElement);
    }

}
