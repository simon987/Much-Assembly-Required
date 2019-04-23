package net.simon987.server;

import java.io.File;

public class ConfigHelper {
    public static ServerConfiguration getConfig() {
        File workingDir = new File("");
        File file = new File(workingDir.getAbsolutePath(), "config.properties");

        if (!file.exists()) {
            File fallback = new File("Server/src/main/resources/", file.getName());
            if (fallback.exists()) {
                file = fallback;
            } else {
                throw new AssertionError("'config.properties' and " +
                        "'Server/src/main/resources/config.properties' cannot be found with working directory: " +
                        workingDir.getAbsolutePath());
            }
        }

        return new ServerConfiguration(file.getAbsolutePath());
    }

}
