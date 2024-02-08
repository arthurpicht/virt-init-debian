package de.arthurpicht.virtInitDeb.helper;

import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardErrorHandler;
import de.arthurpicht.processExecutor.outputHandler.generalOutputHandler.GeneralStandardOutHandler;
import de.arthurpicht.virtInitDeb.config.GeneralConfig;

public class OutputHandlerHelper {

    public static GeneralStandardOutHandler obtainGeneralStandardOutHandler(GeneralConfig generalConfig) {
        GeneralStandardOutHandler.Builder outBuilder = new GeneralStandardOutHandler.Builder();
        if (generalConfig.hasLogger()) {
            outBuilder.withLogger(generalConfig.getLogger());
        }
        if (generalConfig.isOutputToConsole()) {
            outBuilder.withConsoleOutput();
        }
        return outBuilder.build();
    }

    public static GeneralStandardErrorHandler obtainGeneralStandardErrorHandler(GeneralConfig generalConfig) {
        GeneralStandardErrorHandler.Builder errorBuilder = new GeneralStandardErrorHandler.Builder();
        if (generalConfig.hasLogger()) {
            errorBuilder.withLogger(generalConfig.getLogger());
        }
        if (generalConfig.isOutputToConsole()) {
            errorBuilder.withConsoleOutput();
        }
        return errorBuilder.build();
    }

}
