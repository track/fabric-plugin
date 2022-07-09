package net.analyse.plugin;

import net.analyse.plugin.command.HelloWorldCommand;
import net.analyse.plugin.util.ModCommandRegister;
import net.analyse.plugin.util.ModEventsRegister;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analyse implements ModInitializer {
	public static final String MOD_ID = "analyse";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// register the command
		ModCommandRegister.registerCommands();
		ModEventsRegister.registerEvents();
	}
}
