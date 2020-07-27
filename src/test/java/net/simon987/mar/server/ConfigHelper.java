package net.simon987.mar.server;

import java.io.File;

public class ConfigHelper {
    public static ServerConfiguration getConfig() {
        File workingDir = new File("");
        File file = new File(workingDir.getAbsolutePath(), "config.properties");

        if (!file.exists()) {
            File fallback = new File("src/main/resources/", file.getName());
            if (fallback.exists()) {
                file = fallback;
            } else {
                throw new AssertionError("'config.properties' and " +
                        "'src/main/resources/config.properties' cannot be found with working directory: " +
                        workingDir.getAbsolutePath());
            }
        }

        return new ServerConfiguration(file.getAbsolutePath());
    }

}
