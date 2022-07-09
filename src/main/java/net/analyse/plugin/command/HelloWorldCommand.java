package net.analyse.plugin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import static net.minecraft.server.command.CommandManager.*;

public class HelloWorldCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            CommandManager.literal("analyse").executes(context -> {
                context.getSource().sendFeedback(Text.literal("Analyse Plugin"), true);
                return 1;
            })
            .then(
                    CommandManager.literal("setup").then(
                            CommandManager.argument("token", StringArgumentType.string()).executes(context -> {
                                context.getSource().sendFeedback(Text.literal("Hello " + StringArgumentType.getString(context, "token")), true);
                                return 1;
                            })
                    )
            )
        );
    }

}
