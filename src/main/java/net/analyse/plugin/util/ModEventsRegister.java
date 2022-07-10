package net.analyse.plugin.util;

import net.analyse.plugin.Analyse;
import net.analyse.plugin.events.PlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ModEventsRegister {
    private final Analyse plugin;

    public ModEventsRegister(Analyse plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register(new PlayerEvents(plugin));
        ServerPlayConnectionEvents.DISCONNECT.register(new PlayerEvents(plugin));
    }
}
