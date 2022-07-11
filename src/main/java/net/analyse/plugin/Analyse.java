package net.analyse.plugin;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import net.analyse.plugin.command.AnalyseCommand;
import net.analyse.plugin.util.AnalyseConfig;
import net.analyse.plugin.util.ModEventsRegister;
import net.analyse.sdk.AnalyseSDK;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.response.GetPluginResponse;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import redis.clients.jedis.JedisPooled;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static net.analyse.plugin.util.EncryptUtil.generateEncryptionKey;

public class Analyse implements ModInitializer {
	private final Map<UUID, Date> activeJoinMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());
	private final Map<UUID, String> playerDomainMap = new TCustomHashMap<>(new IdentityHashingStrategy<>());

	private AnalyseSDK core = null;

	private boolean setup;

	private String serverToken;
	private String encryptionKey;
	private JedisPooled redis = null;

	// TODO: Auto find the actual plugin version.
	private final String PLUGIN_VERSION = "1.0.0";

	private final String API_HEADER = "Analyse v" + PLUGIN_VERSION + " / Fabric v1.19";
	private int incrementalVersion = Integer.parseInt(PLUGIN_VERSION.replace(".", ""));


	public final String MOD_ID = "analyse";
	public final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private final File MOD_PATH = new File("./mods/" + MOD_ID);

	private AnalyseConfig config;

	@Override
	public void onInitialize() {
		log("Initializing Analyse...");

		try {
			this.config = saveDefaultConfig();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config", e);
		}

		serverToken = config.getServerToken();
		encryptionKey = config.getEncryptionKey();
		setup = serverToken != null && !serverToken.isEmpty();


		if (!setup) {
			log("Hey! I'm not yet set-up, please run the following command:");
			log("/analyse setup <server-token>");
		} else {
			core = new AnalyseSDK(serverToken, encryptionKey);
			core.setApiHeader(API_HEADER);

			try {
				log("Linked Analyse to " + core.getServer().getName() + ".");
			} catch (ServerNotFoundException e) {
				log("The server linked no longer exists.");
			}
		}

		serverToken = config.getServerToken();
		encryptionKey = config.getEncryptionKey();

		if (encryptionKey == null || encryptionKey.isEmpty()) {
			encryptionKey = generateEncryptionKey(64);

			try {
				ConfigurationNode configurationNode = config.getConfigurationNode();
				configurationNode.node("encryption-key").set(encryptionKey);
				config.getLoader().save(configurationNode);
			} catch (ConfigurateException e) {
				throw new RuntimeException(e);
			}

			log("Generated encryption key.");
			reloadConfig();
		}

		// register our utils.
		registerCommands();
		new ModEventsRegister(this).registerEvents();

		if(core != null) {
			GetPluginResponse corePluginVersion = core.getPluginVersion();
			if(corePluginVersion.getVersionNumber() > incrementalVersion) {
				log(String.format("This server is running v%s, an outdated version of Analyse.", PLUGIN_VERSION));
				log(String.format("Download v%s at: %s", corePluginVersion.getVersionName(), corePluginVersion.getBukkitDownload()));
			}
		}
	}

	public void registerCommands() {
		CommandRegistrationCallback.EVENT.register(new AnalyseCommand(this)::register);
	}

	public void reloadConfig() {
		try {
			config = saveDefaultConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AnalyseConfig saveDefaultConfig() throws Exception {
		if (!MOD_PATH.exists()) {
			MOD_PATH.mkdirs();
		}

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
		CommentedConfigurationNode configNode = loader.load();

		return new AnalyseConfig(loader, configNode);
	}

	public void log(String message) {
		LOGGER.info("[Analyse] " + message);
	}

	public void debug(String message) {
		if(!getConfig().debug()) return;
		log("Debug: " + message);
	}

	public AnalyseSDK setup(String token) {
		core = new AnalyseSDK(token, encryptionKey);
		core.setApiHeader(API_HEADER);
		return core;
	}

	public AnalyseConfig getConfig() {
		return config;
	}

	public AnalyseSDK getCore() {
		return core;
	}

	public boolean isSetup() {
		return setup;
	}

	public void setSetup(boolean setup) {
		this.setup = setup;
	}

	public Map<UUID, Date> getActiveJoinMap() {
		return activeJoinMap;
	}

	public Map<UUID, String> getPlayerDomainMap() {
		return playerDomainMap;
	}

	public void clearPlayerCache(UUID playerUuid) {
		getPlayerDomainMap().remove(playerUuid);
		getActiveJoinMap().remove(playerUuid);
		core.getExcludedPlayers().remove(playerUuid);
		core.getPlayerStatistics(playerUuid).clear();
	}
}
