package net.analyse.plugin.events;

import net.analyse.plugin.Analyse;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.request.object.PlayerStatistic;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerEvents implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {
    private final Analyse plugin;

    public PlayerEvents(Analyse plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if(!plugin.isSetup()) {
            return;
        }

        ServerPlayerEntity player = handler.getPlayer();
        if(plugin.getConfig().getExcludedPlayers().contains(player.getUuid().toString())) {
            return;
        }

        if(!plugin.getConfig().advancedMode()) {
            // TODO: Find event for this.
            String hostName = null;
            plugin.debug("Player connecting via: " + hostName);
            plugin.getPlayerDomainMap().put(player.getUuid(), hostName);
        }

        plugin.debug("Tracking " + player.getName() + " to current time");
        plugin.getActiveJoinMap().put(player.getUuid(), new Date());

        try {
            System.out.println(player.getName() + " has joined " + plugin.getCore().getServer().getName());
        } catch (ServerNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        if (!plugin.isSetup()) return;

        ServerPlayerEntity player = handler.getPlayer();

        if(plugin.getConfig().getExcludedPlayers().contains(player.getUuid().toString())) {
            return;
        }

        new Thread(() -> {
            final UUID playerUuid = player.getUuid();
            final String playerName = player.getName().getString();

            if(! plugin.getActiveJoinMap().containsKey(playerUuid)) {
                plugin.debug("Player " + playerName + " has left the server, but was never tracked.");
                return;
            }

            final Date joinedAt = plugin.getActiveJoinMap().get(playerUuid);
            final Date quitAt = new Date();
            final String playerIp = player.getIp();
            final long seconds = (quitAt.getTime()-joinedAt.getTime()) / 1000;
            final String domainConnected;

            plugin.debug(" ");
            plugin.debug("Preparing analytics for " + playerName + " (" + playerUuid + ")..");

            // TODO: Enable fetching from redis.
            domainConnected = plugin.getPlayerDomainMap().getOrDefault(playerUuid, null);
            plugin.debug(" - Connected from: '" + domainConnected + "' (Cache).");

            plugin.debug(" - Joined at: " + joinedAt);
            plugin.debug(" - Player IP: " + playerIp);
            plugin.debug(" ");

            final List<PlayerStatistic> playerStatistics = plugin.getCore().getPlayerStatistics(playerUuid);

            for (PlayerStatistic playerStatistic : playerStatistics) {
                plugin.debug(" > Custom statistic %" + playerStatistic.getKey() + "% with value: " + playerStatistic.getValue());
            }

            plugin.debug(" ");

            final int minSessionDuration = plugin.getConfig().getMinSessionDuration();
            if(seconds >= minSessionDuration) {
                try {
                    // TODO: Find how to get first join date from world.
                    plugin.getCore().sendPlayerSession(playerUuid, playerName, joinedAt, domainConnected, playerIp, null, playerStatistics);
                    plugin.debug("Sent player session data to Analyse!");
                } catch (ServerNotFoundException e) {
                    plugin.setSetup(false);
                    plugin.LOGGER.warn("The server specified no longer exists.");
                }
            } else {
                plugin.debug("Skipping data as they haven't played for long enough (" + minSessionDuration + " " + (minSessionDuration == 1 ? "second" : "seconds") + " minimum).");
                plugin.debug("You can change this in the config.yml.");
            }
            plugin.debug(" ");
            plugin.clearPlayerCache(playerUuid);
        }).start();
    }
}
