# virt-init-debian

> This project is under development.

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
        .build("hello-world");

InstallConfig installConfig = new InstallConfig.Builder()
        .withUserFullName("Joe Dummy")
        .withUserName("jdummy")
        .build("ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAICVYEjfT3BdV4gOOPTeLp2ko9w2Sd5s3tfAs0you7/Wx dummy@machine");

new VirtInitDeb(generalConfig, vmConfig, installConfig).execute();
```

## Build

Simple `gradle` build as usual:

    gradle jar

JDK 17 and gradle 8.5 required

## Configuration

Configuration is done by three configuration classes each providing a respective builder. See javadocs for more info.

* General configuration for `virt-init-debian`: [GeneralConfig](src/main/java/de/arthurpicht/virtInitDeb/config/GeneralConfig.java)
* Configuration of the VM to be created: [VmConfig](src/main/java/de/arthurpicht/virtInitDeb/config/VmConfig.java)
* Configuration of the preseeded Debian installation: [InstallConfig](src/main/java/de/arthurpicht/virtInitDeb/config/InstallConfig.java)

## Background

This functionality implements a wrapper around `virt-install`. The unattended installation of Debian is done by taking
advantage of the Debian preseed mechanism. A preseed configuration file is generated an passed to virt-install.
Some further basic configuration of the newly created machine is done by a
post installation bash script which is injected by preseed.

## Shared folders

The shared folder option facilitates the virtiofs feature of qemu/kvm. When at least one shared folder is configured,
the vm will be configured for shared memory as well as the virtiofs filesystem is added. If a mountpoint is specified
the respective directory is created and also an entry in /etc/fstab is generated. The shared folder will be mounted
automatically on startup.

## Acknowledgements

This project is heavily inspired by https://github.com/pin/debian-vm-install, which provides a bash script for the
same purpose.