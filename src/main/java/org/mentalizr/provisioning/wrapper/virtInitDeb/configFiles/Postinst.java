package org.mentalizr.provisioning.wrapper.virtInitDeb.configFiles;

import de.arthurpicht.utils.core.strings.StringSubstitutor;
import de.arthurpicht.utils.core.strings.StringSubstitutorConfiguration;
import org.mentalizr.provisioning.wrapper.virtInitDeb.config.InstallConfig;

public class Postinst {

    private final InstallConfig installConfig;

    public Postinst(InstallConfig installConfig) {
        this.installConfig = installConfig;
    }

    public String asString() {
        String template = getTemplate();
        StringSubstitutorConfiguration substitutorConfig
                = TemplateHelper.getStringSubstitutorConfiguration(this.installConfig);
        return StringSubstitutor.substitute(template, substitutorConfig);
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

}
