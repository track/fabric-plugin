package net.analyse.plugin;

import net.analyse.plugin.util.AnalyseConfig;
import net.analyse.plugin.util.ModCommandRegister;
import net.analyse.plugin.util.ModEventsRegister;
import net.fabricmc.api.ModInitializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Analyse implements ModInitializer {
	public static final String MOD_ID = "analyse";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private File MOD_PATH = new File("./mods/" + MOD_ID);

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
		TypeSerializerCollection serializerCollection = TypeSerializerCollection.create();

		ConfigurationOptions options = ConfigurationOptions.defaults()
				.withSerializers(serializerCollection);

		ConfigurationNode configNode = YAMLConfigurationLoader.builder()
				.setDefaultOptions(options)
				.setFile(getBundledFile("config.yml"))
				.build()
				.load();

		return new AnalyseConfig(configNode);
	}

	private File getBundledFile(String name) {
		File file = new File(MOD_PATH, name);

		if (!file.exists()) {
			MOD_PATH.mkdir();
			try (InputStream in = Analyse.class.getResourceAsStream("/" + name)) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

}
