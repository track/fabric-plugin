package net.analyse.plugin.util;

import net.analyse.plugin.events.PlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ModEventsRegister {
    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register(new PlayerEvents());
        ServerPlayConnectionEvents.DISCONNECT.register(new PlayerEvents());
    }
}
