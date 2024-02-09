package de.arthurpicht.virtInitDeb.config;

public record SharedFolder(String targetTag, String sourceDir, String mountPoint) {

    public SharedFolder(String sourceDir, String targetTag) {
        this(targetTag, sourceDir, null);
    }

    public boolean hasMountPoint() {
        return this.mountPoint != null;
    }

}
