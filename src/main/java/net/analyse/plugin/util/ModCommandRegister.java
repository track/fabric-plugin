package net.analyse.plugin.util;

import net.analyse.plugin.command.HelloWorldCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommandRegister {

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(HelloWorldCommand::register);
    }
}
