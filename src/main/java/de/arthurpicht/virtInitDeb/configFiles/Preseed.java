package de.arthurpicht.virtInitDeb.configFiles;

import de.arthurpicht.utils.core.strings.StringSubstitutor;
import de.arthurpicht.utils.core.strings.StringSubstitutorConfiguration;
import de.arthurpicht.virtInitDeb.config.InstallConfig;

public class Preseed {

    private final InstallConfig installConfig;

    public Preseed(InstallConfig installConfig) {
        this.installConfig = installConfig;
    }

    public String asString() {
        String mainTemplate = getTemplate();
        StringSubstitutorConfiguration substitutorConfig
                = TemplateHelper.getStringSubstitutorConfiguration(this.installConfig);
        String preseed = StringSubstitutor.substitute(mainTemplate, substitutorConfig);
        if (this.installConfig.hasStaticIpConfiguration()) {
            String staticNetworkTemplate = getStaticNetworkTemplate();
            String networkPreseed = StringSubstitutor.substitute(staticNetworkTemplate, substitutorConfig);
            preseed = preseed + "\n" + networkPreseed;
        }
        return preseed;
    }

    private String getTemplate() {
        return """
                d-i debian-installer/language string en
                d-i debian-installer/country string {{COUNTRY}}
                d-i debian-installer/locale string {{LOCALE}}
                d-i keyboard-configuration/xkb-keymap select {{KEYBOARD}}
                                
                # Choose an network interface that has link if possible.
                d-i netcfg/choose_interface select auto
                                
                # Disable that annoying WEP key dialog.
                d-i netcfg/wireless_wep string
                                
                # Mirror settings.
                d-i mirror/country string manual
                d-i mirror/http/hostname string {{MIRROR}}
                d-i mirror/http/directory string /debian
                d-i mirror/http/proxy string {{PROXY}}
                                
                # Root account setup. You can set password in plain-text or pre-encrypted.
                # d-i passwd/root-login boolean false
                d-i passwd/root-password password {{ROOT_PASSWORD}}
                d-i passwd/root-password-again password {{ROOT_PASSWORD}}
                                
                # User account setup.
                #d-i passwd/make-user boolean false
                d-i passwd/user-fullname string {{USER_FULL_NAME}}
                d-i passwd/username string {{USER_NAME}}
                d-i passwd/user-password password {{USER_PASSWORD}}
                d-i passwd/user-password-again password {{USER_PASSWORD}}
                # Password login is disabled.
                # d-i passwd/user-password-crypted password !
                                
                # Controls whether or not the hardware clock is set to UTC.
                d-i clock-setup/utc boolean true
                # See the contents of /usr/share/zoneinfo/ for valid values.
                d-i time/zone string {{TIMEZONE}}
                # Controls whether to use NTP to set the clock during the install.
                d-i clock-setup/ntp boolean true
                                
                # Simple non-LVM, all files in one partition.
                d-i partman-auto/method string regular
                d-i partman-auto/choose_recipe select atomic
                d-i partman-partitioning/confirm_write_new_label boolean true
                d-i partman/choose_partition select finish
                d-i partman/confirm boolean true
                d-i partman/confirm_nooverwrite boolean true
                                
                # Do not install recommended packages by default.
                d-i base-installer/install-recommends boolean false
                tasksel tasksel/first multiselect
                                
                # Individual additional packages to install.
                # ACPI packages are needed for `virsh shutdown <domain>` to work.
                d-i pkgsel/include string openssh-server ca-certificates acpid acpi-support-base {{APT_PACKAGES}}
                popularity-contest popularity-contest/participate boolean false
                                
                # Bootloader installation.
                d-i grub-installer/bootdev string /dev/vda
                                
                # Run postinst.sh in /target just before the install finishes.
                d-i preseed/late_command string cp postinst.sh postinst.tar.gz /target/tmp/ && chmod 755 /target/tmp/postinst.sh && in-target /tmp/postinst.sh
                                
                # Avoid that last message about the install being complete.
                d-i finish-install/reboot_in_progress note
                
                """;
    }

    private String getStaticNetworkTemplate() {
        return """
                # Static network configuration
                d-i netcfg/disable_autoconfig boolean true
                d-i netcfg/get_ipaddress string {{IP}}
                d-i netcfg/get_netmask string {{NETMASK}}
                d-i netcfg/get_gateway string {{GATEWAY}}
                d-i netcfg/get_nameservers string {{NAMESERVER}}
                d-i netcfg/confirm_static boolean true
                """;
    }

}
