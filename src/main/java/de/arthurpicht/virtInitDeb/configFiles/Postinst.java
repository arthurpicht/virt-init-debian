package de.arthurpicht.virtInitDeb.configFiles;

import de.arthurpicht.utils.core.strings.StringSubstitutor;
import de.arthurpicht.utils.core.strings.StringSubstitutorConfiguration;
import de.arthurpicht.utils.io.nio2.FileUtils;
import de.arthurpicht.virtInitDeb.config.InstallConfig;
import de.arthurpicht.virtInitDeb.config.SharedFolder;
import de.arthurpicht.virtInitDeb.config.VmConfig;
import de.arthurpicht.virtInitDeb.helper.FileHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Postinst {

    private final VmConfig vmConfig;
    private final InstallConfig installConfig;

    public Postinst(VmConfig vmConfig, InstallConfig installConfig) {
        this.vmConfig = vmConfig;
        this.installConfig = installConfig;
    }

    public String asString() {
        String template = getTemplate();
        StringSubstitutorConfiguration substitutorConfig
                = TemplateHelper.getStringSubstitutorConfiguration(this.installConfig);
        StringBuilder postinst = new StringBuilder(StringSubstitutor.substitute(template, substitutorConfig));
        for (SharedFolder sharedFolder : this.vmConfig.getSharedFolderList()) {
            postinst.append(mountFilesystem(sharedFolder));
        }
        if (this.installConfig.isUserSudo()) {
            postinst.append(addUserToSudoGroup());
        }
        return postinst.toString();
    }

    private String getTemplate() {
        return """
        #!/bin/sh
                        
        # Setup console, remove timeout on boot.
        sed -i 's/GRUB_CMDLINE_LINUX_DEFAULT=.*/GRUB_CMDLINE_LINUX_DEFAULT="console=ttyS0"/g; s/TIMEOUT=5/TIMEOUT=0/g' /etc/default/grub
        update-grub
                        
        # Unpack postinst.tar.gz
        tar -x -v -z -C/tmp -f /tmp/postinst.tar.gz
                        
        # Install SSH key for configured user
        mkdir -m700 /home/{{USER_NAME}}/.ssh
        cat /tmp/postinst/authorized_keys > /home/{{USER_NAME}}/.ssh/authorized_keys
        chown -R {{USER_NAME}}:{{USER_NAME}} /home/{{USER_NAME}}/.ssh
                        
        # delete postinst dir
        rm -rf /tmp/postinst
                        
        # Do not install recommended packages by default
        cat > /etc/apt/apt.conf.d/01norecommend << EOF
        APT::Install-Recommends "0";
        APT::Install-Suggests "0";
        EOF
                
        """;
    }

    private String mountFilesystem(SharedFolder sharedFolder) {
        if (!sharedFolder.hasMountPoint()) return "";
        String string = "# Prerequisites for shared folder with tag '" + sharedFolder.targetTag() + "'\n";
        Path userHome = Paths.get("/home").resolve(this.installConfig.getUserName());
        Path mountPoint = Paths.get(sharedFolder.mountPoint());
        string += "mkdir -p " + mountPoint.toAbsolutePath() + "\n";
        if (FileHelper.isSubdirectory(userHome, mountPoint)) {
            Path subdirectoryOfUserHome = FileHelper.getFirstSubdirectory(mountPoint, userHome);
            String userName = this.installConfig.getUserName();
            string += "chown -R " + userName + ":" + userName + " " + subdirectoryOfUserHome + "\n";
        }
        string += "echo \"" + sharedFolder.targetTag() + " " + sharedFolder.mountPoint() + " virtiofs\" >> /etc/fstab\n";
        return string;
    }

    private String addUserToSudoGroup() {
        return "\nadduser " + this.installConfig.getUserName() + " sudo\n";
    }

}
