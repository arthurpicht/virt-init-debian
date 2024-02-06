package org.mentalizr.provisioning.wrapper.virtInitDeb.core;

import de.arthurpicht.processExecutor.ProcessExecution;
import de.arthurpicht.processExecutor.ProcessExecutionException;
import de.arthurpicht.processExecutor.ProcessResultCollection;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardErrorHandler;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardOutHandler;
import org.mentalizr.provisioning.wrapper.virtInitDeb.config.GeneralConfig;
import org.mentalizr.provisioning.wrapper.virtInitDeb.config.VmConfig;
import org.mentalizr.provisioning.wrapper.virtInitDeb.core.Const;
import org.mentalizr.provisioning.wrapper.virtInitDeb.helper.OutputHandlerHelper;

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
        String string = "virt-install " +
                        "--connect=qemu:///system " +
                        "--name=" + this.vmConfig.getVmName() + " " +
                        "--ram=" + this.vmConfig.getRam() + " " +
                        "--vcpus=" + this.vmConfig.getCpus() + " " +
                        "--disk size=16,path=" + this.vmConfig.getDiskPath().toAbsolutePath() + ",bus=virtio,cache=none " +
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
                        "--noautoconsole";
        command.add(string);
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
