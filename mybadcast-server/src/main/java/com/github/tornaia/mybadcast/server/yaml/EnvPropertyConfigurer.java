package com.github.tornaia.mybadcast.server.yaml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class EnvPropertyConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(EnvPropertyConfigurer.class);

    public static void init(String[] args) {
        if (isRunningInCloudFoundry()) {
            LOG.info("Running in cloud, nothing to do");
            return;
        }

        Optional<String> optionalCfConfigFileName = getConfigFileName(args);
        if (!optionalCfConfigFileName.isPresent()) {
            LOG.error("Please use --cf-config= argument for defining your manifest yml file for local development");
            System.exit(1);
            return;
        }

        String cfConfigFileName = optionalCfConfigFileName.get();
        LOG.info("Configure app: {}", cfConfigFileName);

        EnvPropertyConfigurer envPropertyConfigurer = new EnvPropertyConfigurer();
        try (InputStream inputStream = Files.newInputStream(Paths.get(cfConfigFileName))) {
            envPropertyConfigurer.processFile(inputStream);
        } catch (IOException e) {
            LOG.error("Failed to read manifest yml file: {}", cfConfigFileName);
            System.exit(2);
        }
    }

    private static Optional<String> getConfigFileName(String[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg.matches("^--cf-config=.*$"))
                .map(arg -> arg.replace("--cf-config=", ""))
                .findFirst();
    }

    private static boolean isRunningInCloudFoundry() {
        return System.getenv("VCAP_APPLICATION") != null;
    }

    private void processFile(InputStream inputStream) {
        YamlReader yamlReader = new YamlReader();
        Map<String, String> variables = yamlReader.read(inputStream);
        variables.forEach(this::setProperty);
    }

    private void setProperty(String key, String value) {
        LOG.info("Set system property {} to {}", key, value);
        System.setProperty(key, value);
    }
}
