package org.mentalizr.provisioning.wrapper.virtInitDeb.helper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    public static Path getHomeDir() {
        return Paths.get(System.getProperty("user.home"));
    }

}
