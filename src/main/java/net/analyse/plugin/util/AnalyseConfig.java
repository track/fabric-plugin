package net.analyse.plugin.util;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyseConfig {
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode configurationNode;

    private String serverToken;
    private String encryptionKey;
    private boolean debug;
    private boolean useServerFirstJoinDate;
    private List<String> excludedPlayers;
    private List<String> enabledStats;
    private int minSessionDuration;
    private boolean advancedMode;

    public AnalyseConfig(ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationNode config) {
        this.loader = loader;
        this.configurationNode = config;

        ConfigurationNode serverNode = config.node("server");

        serverToken = serverNode.node("token").getString();
        encryptionKey = config.node("encryption-key").getString();
        debug = config.node("debug").getBoolean(false);
        useServerFirstJoinDate = config.node("use-server-first-join-date").getBoolean(false);
        excludedPlayers = config.node("excluded-players").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        enabledStats = config.node("enabled-stats").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        minSessionDuration = config.node("minimum-session-duration").getInt(0);
        advancedMode = config.node("advanced").node("enabled").getBoolean(false);
    }

    public String getServerToken() {
        return serverToken;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public boolean debug() {
        return debug;
    }

    public boolean useServerFirstJoinDate() {
        return useServerFirstJoinDate;
    }

    public List<String> getExcludedPlayers() {
        return excludedPlayers;
    }

    public List<String> getEnabledStats() {
        return enabledStats;
    }

    public int getMinSessionDuration() {
        return minSessionDuration;
    }

    public boolean advancedMode() {
        return advancedMode;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
        return loader;
    }

    public ConfigurationNode getConfigurationNode() {
        return configurationNode;
    }
}
