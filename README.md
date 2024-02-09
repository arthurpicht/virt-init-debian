# virt-init-debian

A java lib for fully automated creation of qemu/KVM based VMs with unattended installation of Debian.

Features:

* creation of a configurable virtual machine (e.g. memory, disk space, cpus, mac, network, ...)
* unattended installation of debian 12 (bookworm) directly from debian installer, no ISO or vm image needed or downloaded
* automatic installation of ssh public key for created unprivileged user
* configurable shared folders with optional `/etc/fstab` configuration to be mounted on startup
* serial console configured by default
* no graphics configured in vm as intended for server purposes

## Hello world

A very simple configuration based on default values:

```java
GeneralConfig generalConfig = new GeneralConfig.Builder()
        .build();

VmConfig vmConfig = new VmConfig.Builder()
        .build("hello-world", "eb:3c:1a:c2:d2:36");

InstallConfig installConfig = new InstallConfig.Builder()
        .withUserFullName("Joe Dummy")
        .withUserName("jdummy")
        .build("ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAICVYEjfT3BdV4gOOPTeLp2ko9w2Sd5s3tfAs0you7/Wx dummy@machine");

new VirtInitDeb(generalConfig, vmConfig, installConfig).execute();
```

## Background

This functionality implements a wrapper around `virt-install`. The unattended installation of Debian is done by taking
advantage of the Debian preseed mechanism. Some further basic configuration of the newly created machine is done by a
post installation bash script which is injected by preseed.

## Shared folders

The shared folder option facilitates the virtiofs feature of qemu/kvm. When at least one shared folder is configured,
the vm will be configured for shared memory as well as the virtiofs filesystem is added. If a mountpoint is specified
the respective directory is created and also an entry in /etc/fstab is generated. The shared folder will be mounted
automatically on startup.
