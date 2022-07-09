package net.analyse.plugin.events;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerEvents implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        System.out.println(handler.getPlayer().getName() + " has joined the server!");

    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        System.out.println(handler.getPlayer().getName() + " has left the server.");
    }
}
