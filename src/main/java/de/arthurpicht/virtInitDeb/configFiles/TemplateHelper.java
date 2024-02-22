package de.arthurpicht.virtInitDeb.configFiles;

import de.arthurpicht.utils.core.strings.StringSubstitutorConfiguration;
import de.arthurpicht.virtInitDeb.config.InstallConfig;

public class TemplateHelper {

    public static StringSubstitutorConfiguration getStringSubstitutorConfiguration(InstallConfig installConfig) {
        return new StringSubstitutorConfiguration.Builder()
                .withSubstitution("LOCALE", installConfig.getLocale())
                .withSubstitution("COUNTRY", installConfig.getCountry())
                .withSubstitution("KEYBOARD", installConfig.getKeyboard())
                .withSubstitution("MIRROR", installConfig.getMirror())
                .withSubstitution("PROXY", installConfig.getProxy())
                .withSubstitution("ROOT_PASSWORD", installConfig.getRootPassword())
                .withSubstitution("USER_FULL_NAME", installConfig.getUserFullName())
                .withSubstitution("USER_NAME", installConfig.getUserName())
                .withSubstitution("USER_PASSWORD", installConfig.getUserPassword())
                .withSubstitution("TIMEZONE", installConfig.getTimeZone())
                .withSubstitution("APT_PACKAGES", installConfig.getAptPackages())
                .withSubstitution("IP", installConfig.getStaticIpConfiguration().ip())
                .withSubstitution("NETMASK", installConfig.getStaticIpConfiguration().netmask())
                .withSubstitution("GATEWAY", installConfig.getStaticIpConfiguration().gateway())
                .withSubstitution("NAMESERVER", installConfig.getStaticIpConfiguration().nameserver())
                .build();
    }

}
