package de.arthurpicht.virtInitDeb;

import de.arthurpicht.linuxWrapper.core.TarCreate;
import de.arthurpicht.processExecutor.ProcessExecutionException;
import de.arthurpicht.processExecutor.ProcessResultCollection;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardErrorHandler;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardOutHandler;
import de.arthurpicht.utils.io.tempDir.TempDir;
import de.arthurpicht.virtInitDeb.config.GeneralConfig;
import de.arthurpicht.virtInitDeb.config.InstallConfig;
import de.arthurpicht.virtInitDeb.config.VmConfig;
import de.arthurpicht.virtInitDeb.configFiles.Postinst;
import de.arthurpicht.virtInitDeb.configFiles.Preseed;
import de.arthurpicht.virtInitDeb.core.AwaitVmInstallation;
import de.arthurpicht.virtInitDeb.core.Const;
import de.arthurpicht.virtInitDeb.core.VirtInitDebException;
import de.arthurpicht.virtInitDeb.core.VirtInstallWrapper;
import de.arthurpicht.virtInitDeb.helper.OutputHandlerHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class VirtInitDeb {

    private static final String postinstSubDirName = "postinst";
    private static final String postinstTarGzFileName = "postinst.tar.gz";

    private final GeneralConfig generalConfig;
    private final VmConfig vmConfig;
    private final InstallConfig installConfig;
    private final TempDir tempDir;

    public VirtInitDeb(GeneralConfig generalConfig, VmConfig vmConfig, InstallConfig installConfig) {
        this.generalConfig = generalConfig;
        this.vmConfig = vmConfig;
        this.installConfig = installConfig;
        this.tempDir = new TempDir.Creator()
                .withParentDir(generalConfig.getTempDirParent())
                .withTempDirPrefix("virt-init-debian-")
                .withAutoRemove(!generalConfig.isKeepTempDir())
                .create();
    }

    public void execute() {
        Path postInstDir = createPostInstDir();

        createAuthorizedKeyFile(postInstDir);

        createPostinstTar();

        createPressedFile();

        createPostinstFile();

        executeVirtInstall();

        if (this.generalConfig.hasLogger()) {
            this.generalConfig.getLogger().info(
                    "The unattended debian installation process begins now and will take some minutes.");
            this.generalConfig.getLogger().info(
                    "You can follow by connecting virtual console: 'virsh console "
                    + this.vmConfig.getVmName() + "'");
        }

        if (this.generalConfig.isWaitForInstallationCompleted()) {
            AwaitVmInstallation.execute(this.vmConfig.getVmName());
            if (this.generalConfig.hasLogger()) {
                this.generalConfig.getLogger().info("VM is shut down. Unattended creation of VM "
                                                    + this.vmConfig.getVmName() + " finished.");
            }
        }
    }

    private Path createPostInstDir() {
        Path postInstDir = null;
        try {
            postInstDir = this.tempDir.asPath().resolve("postinst");
            Files.createDirectory(postInstDir);
        } catch (IOException e) {
            throw new RuntimeException("Error on creating dir [" + postInstDir + "]: " + e.getMessage(), e);
        }
        return postInstDir;
    }

    private void createAuthorizedKeyFile(Path postInstDir) {
        Path authorizedKeyPath = postInstDir.resolve("authorized_keys");
        String publicSshKey = this.installConfig.getSshKeyPublic();
        try {
            Files.writeString(authorizedKeyPath, publicSshKey);
        } catch (IOException e) {
            throw new RuntimeException("Error on writing public SSH key to " +
                    "[" + authorizedKeyPath.toAbsolutePath() + "]: " + e.getMessage(), e);
        }
    }

    private void createPressedFile() {
        String preseed = new Preseed(this.installConfig).asString();
        Path preseedFile = this.tempDir.asPath().resolve(Const.PRESEED_FILE_NAME);
        try {
            Files.writeString(preseedFile, preseed);
        } catch (IOException e) {
            throw new RuntimeException("Could not write " + preseedFile.toAbsolutePath() + ": " + e.getMessage(), e);
        }
    }

    private void createPostinstFile() {
        String postinst = new Postinst(this.vmConfig, this.installConfig).asString();
        Path postinstFile = this.tempDir.asPath().resolve(Const.POSTINST_FILE_NAME);
        try {
            Files.writeString(postinstFile, postinst);
        } catch (IOException e) {
            throw new RuntimeException("Could not write " + postinstFile.toAbsolutePath() + ": " + e.getMessage(), e);
        }
    }

    private void executeVirtInstall() {
        VirtInstallWrapper virtInstallWrapper = new VirtInstallWrapper(
                this.generalConfig,
                this.vmConfig,
                this.tempDir.asPath());

        if (this.generalConfig.hasLogger()) {
            this.generalConfig.getLogger().info("Execute command:");
            for (String commandPart : virtInstallWrapper.getCommand()) {
                this.generalConfig.getLogger().info(commandPart + " \\");
            }
        }

        try {
            ProcessResultCollection processResultCollection = virtInstallWrapper.execute();
            if (processResultCollection.isFail())
                throw new VirtInitDebException("System process virt-install exited with a non-zero exit code. See log file for more info.");
        } catch (ProcessExecutionException e) {
            throw new VirtInitDebException("Error on executing virt-install: " + e.getMessage(), e);
        }
    }

    private void createPostinstTar() {

        GeneralStandardOutHandler standardOutHandler =
                OutputHandlerHelper.obtainGeneralStandardOutHandler(this.generalConfig);
        GeneralStandardErrorHandler standardErrorHandler =
                OutputHandlerHelper.obtainGeneralStandardErrorHandler(this.generalConfig);

        TarCreate tarCreate = new TarCreate.TarCreateBuilder()
                .withGzip()
                .withWorkingDir(this.tempDir.asPath())
                .withDestination(postinstTarGzFileName)
                .withSource(postinstSubDirName)
                .withStandardOutHandler(standardOutHandler)
                .withStandardErrorHandler(standardErrorHandler)
                .build();

        if (this.generalConfig.hasLogger()) {
            this.generalConfig.getLogger().info("Execute command [" + tarCreate.getCommandAsString() + "]");
            this.generalConfig.getLogger().info("Command execution in working dir [" + this.tempDir.asPath() + "].");
        }

        try {
            ProcessResultCollection processResultCollection = tarCreate.execute();
            if (processResultCollection.isFail())
                throw new RuntimeException("Executing tar command failed. See log file for more info.");
        } catch (ProcessExecutionException e) {
            throw new RuntimeException("Error on executing tar command: " + e.getMessage(), e);
        }
    }

}
