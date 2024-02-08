package de.arthurpicht.virtInitDeb.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.arthurpicht.utils.core.assertion.MethodPreconditions.assertArgumentNotNull;

public class VmConfig {

    private final String vmName;
    private final int ram;
    private final int cpus;
    private final int diskSize;
    private final Path diskPath;
    private final String mac;
    private final String networkDevice;
    private final String hostname;
    private final String domain;
    private final List<SharedFolder> sharedFolderList;
    public record SharedFolder(String sourceDir, String targetTag, boolean autoMount) {}

    public static class Builder {

        private int ram = 2048;
        private int cpus = 1;
        private int diskSize = 24;
        private Path diskPath = null;
        private String networkDevice = "virbr0";
        private String hostname = null;
        private String domain;
        private final List<SharedFolder> sharedFolderList = new ArrayList<>();

        /**
         * RAM in KB. Default: 2048;
         */
        public Builder withRam(int ram) {
            this.ram = ram;
            return this;
        }

        /**
         * Number of CPUs. Default: 1.
         */
        public Builder withCpus(int cpus) {
            this.cpus = cpus;
            return this;
        }

        /**
         * Disk size in GB. Default: 24.
         */
        public Builder withDiskSize(int diskSize) {
            this.diskSize = diskSize;
            return this;
        }

        /**
         * Path of disk image file, e.g.: /my/path/disk.img
         * Default: /var/lib/libvirt/images/${vm_name}.img
         */
        public Builder withDiskPath(Path diskPath) {
            this.diskPath = diskPath;
            return this;
        }

//        /**
//         * MAC-Address.
//         */
//        public Builder withMac(String mac) {
//            this.mac = mac;
//            return this;
//        }

        /**
         * Connect VM to specified network device. See 'ip address' for network device names - not names as listed with
         * 'virsh net-list'. Default: virbr0
         * @param networkDevice
         * @return
         */
        public Builder withConnectionToNetworkDevice(String networkDevice) {
            this.networkDevice = networkDevice;
            return this;
        }

        /**
         * Hostname. Default: VmName.
         */
        public Builder withHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        /**
         * Domain. Default: ${VmName}.local
         */
        public Builder withDomain(String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * Default: none;
         */
        public Builder withSharedFolder(SharedFolder sharedFolder) {
            this.sharedFolderList.add(sharedFolder);
            return this;
        }

        public VmConfig build(String vmName, String mac) {
            return new VmConfig(
                    vmName,
                    this.ram,
                    this.cpus,
                    this.diskSize,
                    this.diskPath,
                    mac,
                    this.networkDevice,
                    this.hostname,
                    this.domain,
                    this.sharedFolderList
            );
        }
    }

    private VmConfig(
            String vmName,
            int ram,
            int cpus,
            int diskSize,
            Path diskPath,
            String mac,
            String networkDevice,
            String hostname,
            String domain,
            List<SharedFolder> sharedFolderList
    ) {
        assertArgumentNotNull("vmName", vmName);
        assertArgumentNotNull("mac", mac);
        assertArgumentNotNull("network", networkDevice);

        this.vmName = vmName;
        this.ram = ram;
        this.cpus = cpus;
        this.diskSize = diskSize;
        this.diskPath = (diskPath == null ?
                Paths.get("/var/lib/libvirt/images/").resolve(vmName + ".img") : diskPath);
        this.mac = mac;
        this.networkDevice = networkDevice;
        this.hostname = (hostname == null ? vmName : hostname);
        this.domain = (domain == null ? vmName + ".local" : domain);
        this.sharedFolderList = Collections.unmodifiableList(sharedFolderList);
    }

    public String getVmName() {
        return this.vmName;
    }

    public int getRam() {
        return this.ram;
    }

    public int getCpus() {
        return this.cpus;
    }

    public int getDiskSize() {
        return this.diskSize;
    }

    public Path getDiskPath() {
        return this.diskPath;
    }

    public String getMac() {
        return this.mac;
    }

    public String getNetworkDevice() {
        return this.networkDevice;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getDomain() {
        return this.domain;
    }

    public boolean hasSharedFolders() {
        return !this.sharedFolderList.isEmpty();
    }

    public List<SharedFolder> getSharedFolderList() {
        return this.sharedFolderList;
    }

}
