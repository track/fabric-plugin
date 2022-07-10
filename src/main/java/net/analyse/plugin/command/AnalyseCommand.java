package net.analyse.plugin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.analyse.plugin.Analyse;
import net.analyse.sdk.AnalyseSDK;
import net.analyse.sdk.exception.ServerNotFoundException;
import net.analyse.sdk.response.GetServerResponse;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class AnalyseCommand {
    private final Analyse plugin;

    public AnalyseCommand(Analyse plugin) {
        this.plugin = plugin;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            CommandManager.literal("analyse").executes(context -> {

                if(plugin.isSetup()) {
                    try {
                        context.getSource().sendFeedback(Text.literal("Analyse: Connected to " + plugin.getCore().getServer().getName()), true);
                    } catch (ServerNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    return 1;
                }

                context.getSource().sendFeedback(Text.literal("Analyse Plugin"), true);
                return 1;
            })
            .then(
                    CommandManager.literal("setup").then(
                            CommandManager.argument("token", StringArgumentType.string()).executes(context -> {
                                Thread newThread = new Thread(() -> {
                                    String token = StringArgumentType.getString(context, "token");
                                    AnalyseSDK analyseSDK = plugin.setup(token);

                                    try {
                                        GetServerResponse server = analyseSDK.getServer();
                                        context.getSource().sendFeedback(Text.literal("[Analyse] Successfully linked Analyse with " + server.getName() + " server."), true);

                                        ConfigurationNode configurationNode = plugin.getConfig().getConfigurationNode();
                                        ConfigurationNode serverNode = configurationNode.node("server");
                                        serverNode.node("token").set(token);
                                        serverNode.node("id").set(server.getUuid());
                                        plugin.getConfig().getLoader().save(configurationNode);
                                        plugin.setSetup(true);
                                    } catch (ServerNotFoundException e) {
                                        context.getSource().sendFeedback(Text.literal("[Analyse] Sorry that server is invalid."), true);
                                    } catch (SerializationException e) {
                                        throw new RuntimeException(e);
                                    } catch (ConfigurateException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                newThread.start();

                                return 1;
                            })
                    )
            )
        );
    }

}
