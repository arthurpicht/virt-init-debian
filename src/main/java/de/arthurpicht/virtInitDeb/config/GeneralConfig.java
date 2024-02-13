package de.arthurpicht.virtInitDeb.config;

import de.arthurpicht.virtInitDeb.helper.FileHelper;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneralConfig {

    private final Logger logger;
    private final boolean outputToConsole;
    private final Path tempDirParent;
    private final boolean waitForInstallationCompleted;
    private final boolean keepTempDir;

    public static class Builder {

        private Logger logger = null;
        private boolean outputToConsole = false;
        private Path tempDirParent = Paths.get("/var/tmp");
        private boolean waitForInstallationCompleted = false;
        private boolean keepTempDir = false;

        /**
         * Logger. Default: none.
         */
        public Builder withLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Write output to console. Default: false;
         */
        public Builder withOutputToConsole() {
            this.outputToConsole = true;
            return this;
        }

        /**
         * Parent directory for temp directory creation. Default: /var/tmp.
         */
        public Builder withTempFileParent(Path tempDirParent) {
            this.tempDirParent = tempDirParent;
            return this;
        }

        /**
         * Wait for preseeded installation of Debian to be completed. Default: Precess terminates after starting
         * virtual machine for the first time. Installation will be executed in background.
         */
        public Builder waitForInstallationCompleted() {
            this.waitForInstallationCompleted = true;
            return this;
        }

        /**
         * Keep created temp dir for debug purposes.
         * Default: false.
         */
        public Builder keepTempDir() {
            this.keepTempDir = true;
            return this;
        }

        public GeneralConfig build() {
            return new GeneralConfig(
                    this.logger,
                    this.outputToConsole,
                    this.tempDirParent,
                    this.waitForInstallationCompleted,
                    this.keepTempDir
            );
        }
    }

    private GeneralConfig(
            Logger logger,
            boolean outputToConsole,
            Path tempDirParent,
            boolean waitForInstallationCompleted,
            boolean keepTempDir
            ) {
        this.logger = logger;
        this.outputToConsole = outputToConsole;
        this.tempDirParent = (tempDirParent == null ? FileHelper.getHomeDir() : tempDirParent);
        this.waitForInstallationCompleted = waitForInstallationCompleted;
        this.keepTempDir = keepTempDir;
    }

    public boolean hasLogger() {
        return this.logger != null;
    }

    public Logger getLogger() {
        if (this.logger == null)
            throw new IllegalStateException("No logger specified. Check before calling getter method.");
        return this.logger;
    }

    public boolean isOutputToConsole() {
        return this.outputToConsole;
    }

    public Path getTempDirParent() {
        return this.tempDirParent;
    }

    public boolean isWaitForInstallationCompleted() {
        return this.waitForInstallationCompleted;
    }

    public boolean isKeepTempDir() {
        return this.keepTempDir;
    }

}
