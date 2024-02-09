package de.arthurpicht.virtInitDeb.core;

import de.arthurpicht.linuxWrapper.core.ps.Process;
import de.arthurpicht.linuxWrapper.core.ps.Ps;
import de.arthurpicht.linuxWrapper.core.ps.PsWax;
import de.arthurpicht.processExecutor.ProcessResultCollection;
import de.arthurpicht.utils.core.strings.SimplifiedGlob;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AwaitVmInstallation {

    private static final int timeout = 20 * 60 * 1000;
    private static final int sleepTime = 5000;

    public static void execute(String vmName) {
        List<Process> processList = PsWax.execute();
        Optional<Process> process = identifyVmProcess(vmName, processList);
        process.ifPresent(p -> awaitProcessTermination(p.pid()));
    }

    public static boolean hasNoRunningVmProcess(String vmName) {
        List<Process> processList = PsWax.execute();
        Optional<Process> process = identifyVmProcess(vmName, processList);
        return process.isEmpty();
    }

    private static Optional<Process> identifyVmProcess(String vmName, List<Process> processList) {
        Pattern pattern = SimplifiedGlob.compile(
                "/usr/bin/qemu-system*guest=" + vmName + "*");
        return processList.stream()
                .filter(p -> pattern.matcher(p.command()).matches()).findFirst();
    }

    private static void awaitProcessTermination(int pid) {
        long periodWaiting = 0;
        while (true) {
            ProcessResultCollection processResultCollection = Ps.execute(pid);
            if (Ps.noProcessForPidFound(processResultCollection)) {
                break;
            } else if (processResultCollection.isFail()) {
                throw new RuntimeException("Executing ps failed.");
            }
            if (periodWaiting > timeout) {
                throw new RuntimeException("Timeout waiting for vm installation process to finish.");
            }
            try {
                //noinspection BusyWait
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // intentionally do nothing
            }
            periodWaiting += sleepTime;
        }
    }

}
