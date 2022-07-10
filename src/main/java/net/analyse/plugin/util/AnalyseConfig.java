package net.analyse.plugin.util;

import org.spongepowered.configurate.ConfigurationNode;

public class AnalyseConfig {
    private String token;

    public AnalyseConfig(ConfigurationNode config) {
        token = config.node("token").getString();
    }

    public String getToken() {
        return token;
    }
}
