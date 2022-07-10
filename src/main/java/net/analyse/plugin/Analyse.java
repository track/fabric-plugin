package net.analyse.plugin;

import net.analyse.plugin.util.AnalyseConfig;
import net.analyse.plugin.util.ModCommandRegister;
import net.analyse.plugin.util.ModEventsRegister;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Analyse implements ModInitializer {
	public final String MOD_ID = "analyse";
	public final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final File MOD_PATH = new File("./mods/" + MOD_ID);

	private AnalyseConfig config;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Analyse...");

		// register the command
		ModCommandRegister.registerCommands();
		ModEventsRegister.registerEvents();

		// get mod path
		if (!MOD_PATH.exists()) {
			MOD_PATH.mkdirs();
		}

		try {
			this.config = loadConfig();
			LOGGER.info("Config loaded successfully!");
			LOGGER.info("Token is '" + config.getToken() + "'.");
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config", e);
		}
	}

	public void reloadConfig() {
		try {
			config = loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AnalyseConfig loadConfig() throws Exception {
		File file = new File(MOD_PATH, "config.yml");

		if (!file.exists()) {
			MOD_PATH.mkdir();
			try (InputStream in = Analyse.class.getResourceAsStream("/config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Path potentialFile = file.toPath();
		ConfigurationLoader<CommentedConfigurationNode> loader = YamlConfigurationLoader.builder().path(potentialFile).build();

		return new AnalyseConfig(loader.load());
	}

}
