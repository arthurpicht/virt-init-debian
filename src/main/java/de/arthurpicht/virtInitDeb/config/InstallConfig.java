package de.arthurpicht.virtInitDeb.config;

import de.arthurpicht.utils.core.collection.Maps;
import de.arthurpicht.utils.core.collection.Sets;
import de.arthurpicht.utils.core.strings.Strings;

import java.util.*;

public class InstallConfig {
    private final String sshKeyPublic;

    private final String country;
    private final String locale;
    private final String keyboard;
    private final String mirror;
    private final String proxy;
    private final String rootPassword;
    private final String userFullName;
    private final String userName;
    private final String userPassword;
    private final boolean userSudo;
    private final String timeZone;
    private final Set<String> aptPackages;
    private final StaticIpConfiguration staticIpConfiguration;

    public record StaticIpConfiguration(String ip, String netmask, String gateway, String nameserver) {}

    public static class Builder {

        private String country = "DE";
        private String locale = "de_DE.UTF-8";
        private String keyboard = "de";
        private String mirror = "ftp.de.debian.org";
        private String proxy = "";
        private String rootPassword = "secret";
        private String userFullName = "Joe Dummy";
        private String userName = "joe";
        private String userPassword = "secret";
        private boolean userSudo = false;
        private String timeZone = "Europe/Berlin";
        private Set<String> aptPackages = new HashSet<>();
        private StaticIpConfiguration staticIpConfiguration = null;

        /**
         *  Default: DE
         */
        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        /**
         * Default: de_DE.UTF-8
         */
        public Builder withLocale(String locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Keyboard code. Default: de
         */
        public Builder withKeyboard(String keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        /**
         * Default: ftp.de.debian.org
         */
        public Builder withMirror(String mirror) {
            this.mirror = mirror;
            return this;
        }

        public Builder withProxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Default: secret
         */
        public Builder withRootPassword(String rootPassword) {
            this.rootPassword = rootPassword;
            return this;
        }

        /**
         * Default: Joe Dummy
         */
        public Builder withUserFullName(String userFullName) {
            this.userFullName = userFullName;
            return this;
        }

        /**
         * Default: joe
         */
        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        /**
         * Default: secret
         */
        public Builder withUserPassword(String userPassword) {
            this.userPassword = userPassword;
            return this;
        }

        /**
         * Configure sudo for unprivileged user. Default: false;
         */
        public Builder withUserSudo(boolean userSudo) {
            this.userSudo = userSudo;
            return this;
        }

        /**
         * Configure sudo for unprivileged user. Default: false;
         */
        public Builder withUserSudo() {
            this.userSudo = true;
            return this;
        }

        /**
         * See the contents of /usr/share/zoneinfo/ for valid values
         * Default: Europe/Berlin
         */
        public Builder withTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        /**
         * Additional APT packages that will be installed during installation.
         */
        public Builder withAptPackages(String... aptPackages) {
            this.aptPackages = new LinkedHashSet<>(Arrays.asList(aptPackages));
            return this;
        }

        /**
         * A static Ip configuration. Config file /etc/network/interfaces will be generated based hereon.
         * Default: IP-Configuration via DHCP
         */
        public Builder withStaticIpConfiguration(StaticIpConfiguration staticIpConfiguration) {
            this.staticIpConfiguration = staticIpConfiguration;
            return this;
        }

        public InstallConfig build(String sshKeyPublic) {
            return new InstallConfig(
                    sshKeyPublic,
                    this.country,
                    this.locale,
                    this.keyboard,
                    this.mirror,
                    this.proxy,
                    this.rootPassword,
                    this.userFullName,
                    this.userName,
                    this.userPassword,
                    this.userSudo,
                    this.timeZone,
                    this.aptPackages,
                    this.staticIpConfiguration
            );
        }

    }

    private InstallConfig(
            String sshKeyPublic,
            String country,
            String locale,
            String keyboard,
            String mirror,
            String proxy,
            String rootPassword,
            String userFullName,
            String userName,
            String userPassword,
            boolean userSudo,
            String timeZone,
            Set<String> aptPackages,
            StaticIpConfiguration staticIpConfiguration
    ) {

        this.sshKeyPublic = sshKeyPublic;
        this.country = country;
        this.locale = locale;
        this.keyboard = keyboard;
        this.mirror = mirror;
        this.proxy = proxy;
        this.rootPassword = rootPassword;
        this.userFullName = userFullName;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userSudo = userSudo;
        this.timeZone = timeZone;
        this.aptPackages = Collections.unmodifiableSet(aptPackages);
        this.staticIpConfiguration = staticIpConfiguration;
    }

    public String getSshKeyPublic() {
        return this.sshKeyPublic;
    }

    public String getCountry() {
        return this.country;
    }

    public String getLocale() {
        return this.locale;
    }

    public String getKeyboard() {
        return this.keyboard;
    }

    public String getMirror() {
        return this.mirror;
    }

    public String getProxy() {
        return this.proxy;
    }

    public String getRootPassword() {
        return this.rootPassword;
    }

    public String getUserFullName() {
        return this.userFullName;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public boolean isUserSudo() {
        return this.userSudo;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public Set<String> getAptPackages() {
        return this.aptPackages;
    }

    public boolean hasStaticIpConfiguration() {
        return this.staticIpConfiguration != null;
    }

    public StaticIpConfiguration getStaticIpConfiguration() {
        if (this.staticIpConfiguration == null)
            throw new IllegalStateException("StaticIpConfiguration is null. Check before calling.");
        return this.staticIpConfiguration;
    }

}
