package de.arthurpicht.virtInitDeb.core;

import de.arthurpicht.processExecutor.ProcessExecution;
import de.arthurpicht.processExecutor.ProcessExecutionException;
import de.arthurpicht.processExecutor.ProcessResultCollection;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardErrorHandler;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardOutHandler;
import de.arthurpicht.virtInitDeb.config.GeneralConfig;
import de.arthurpicht.virtInitDeb.config.VmConfig;
import de.arthurpicht.virtInitDeb.config.SharedFolder;
import de.arthurpicht.virtInitDeb.helper.OutputHandlerHelper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtInstallWrapper {

    private final GeneralConfig generalConfig;
    private final VmConfig vmConfig;
    private final Path tempDir;
    private final List<String> command;

    public VirtInstallWrapper(GeneralConfig generalConfig, VmConfig vmConfig, Path tempDir) {
        this.generalConfig = generalConfig;
        this.vmConfig = vmConfig;
        this.tempDir = tempDir.toAbsolutePath();
        this.command = buildCommand();
    }

    private List<String> buildCommand() {
        List<String> command = new ArrayList<>();
        command.add("/bin/bash");
        command.add("-c");
        StringBuilder string = new StringBuilder(
                "virt-install " +
                "--connect=qemu:///system " +
                "--name=" + this.vmConfig.getVmName() + " " +
                "--ram=" + this.vmConfig.getRam() + " " +
                "--vcpus=" + this.vmConfig.getCpus() + " " +
                "--disk size=" + this.vmConfig.getDiskSize() + ",path=" + this.vmConfig.getDiskPath().toAbsolutePath() + ",bus=virtio,cache=none " +
                "--initrd-inject=" + tempDir.resolve(Const.PRESEED_FILE_NAME).toAbsolutePath() + " " +
                "--initrd-inject=" + tempDir.resolve(Const.POSTINST_FILE_NAME).toAbsolutePath() + " " +
                "--initrd-inject=" + tempDir.resolve(Const.POSTINST_TAR_GZ).toAbsolutePath() + " " +
                "--location " + Const.LINUX_INSTALLER_URL + " " +
                "--os-variant " + Const.LINUX_VARIANT + " " +
                "--virt-type=kvm " +
                "--controller usb,model=none " +
                "--graphics none " +
                "--network bridge=" + this.vmConfig.getNetworkDevice() + ",mac=" + this.vmConfig.getMac() + ",model=virtio " +
                "--extra-args=\"auto=true hostname=" + this.vmConfig.getHostname() + " domain=" + this.vmConfig.getDomain() + " console=tty0 console=ttyS0,115200n8 serial\" " +
                "--noautoconsole"
        );

        if (this.vmConfig.hasSharedFolders()) {
            string.append(" --memorybacking source.type=memfd,access.mode=shared");
        }
        for (SharedFolder sharedFolder : this.vmConfig.getSharedFolderList()) {
            string.append(" --filesystem source.dir=")
                    .append(sharedFolder.sourceDir())
                    .append(",target.dir=")
                    .append(sharedFolder.targetTag())
                    .append(",accessmode=passthrough,driver.type=virtiofs");
        }

//                        "--filesystem source.dir=/home/apicht/gitrepos/m7r/core,target.dir=m7r_repos_core,accessmode=passthrough,driver.type=virtiofs " +
//                        "--filesystem source.dir=/home/apicht/gitrepos/m7r/infra,target.dir=m7r_repos_infra,accessmode=passthrough,driver.type=virtiofs";

        command.add(string.toString());
        return Collections.unmodifiableList(command);
    }

    public List<String> getCommand() {
        return this.command;
    }

    public ProcessResultCollection execute() throws ProcessExecutionException {
        GeneralStandardOutHandler standardOutHandler
                = OutputHandlerHelper.obtainGeneralStandardOutHandler(this.generalConfig);
        GeneralStandardErrorHandler standardErrorHandler
                = OutputHandlerHelper.obtainGeneralStandardErrorHandler(this.generalConfig);

        return ProcessExecution.execute(
                standardOutHandler, standardErrorHandler, this.command.toArray(new String[0]));
    }

}
