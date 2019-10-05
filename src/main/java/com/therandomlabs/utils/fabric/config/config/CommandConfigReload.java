package com.therandomlabs.utils.fabric.config.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.therandomlabs.utils.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public final class CommandConfigReload {
	@FunctionalInterface
	public interface ConfigReloader {
		void reload(ReloadPhase phase, CommandSource source);
	}

	public enum ReloadPhase {
		PRE,
		POST
	}

	private CommandConfigReload() {}

	public static void client(
			CommandDispatcher<CommandSource> dispatcher, String name, Class<?> configClass
	) {
		client(dispatcher, name, configClass, null);
	}

	public static void client(
			CommandDispatcher<CommandSource> dispatcher, String name, Class<?> configClass,
			ConfigReloader reloader
	) {
		register(dispatcher, name, name, configClass, reloader, true, null);
	}

	public static void server(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass
	) {
		server(dispatcher, name, clientName, configClass, null, null);
	}

	public static void server(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass, String successMessage
	) {
		server(dispatcher, name, clientName, configClass, successMessage, null);
	}

	public static void server(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass, String successMessage, ConfigReloader reloader
	) {
		register(
				dispatcher, name, clientName, configClass, reloader, false,
				successMessage
		);
	}

	private static void register(
			CommandDispatcher<CommandSource> dispatcher, String name, String clientName,
			Class<?> configClass, ConfigReloader reloader, boolean client, String successMessage
	) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal(name).
				requires(source -> source.hasPermissionLevel(client ? 0 : 4)).
				executes(context -> execute(
						context.getSource(), name, clientName, configClass, reloader, client,
						successMessage
				)));
	}

	private static int execute(
			CommandSource source, String name, String clientName, Class<?> configClass,
			ConfigReloader reloader, boolean client, String successMessage
	) {
		if(reloader != null) {
			reloader.reload(ReloadPhase.PRE, source);
		}

		ConfigManager.reloadFromDisk(configClass);

		if(reloader != null) {
			reloader.reload(ReloadPhase.POST, source);
		}

		boolean serverSided = source instanceof ServerCommandSource;

		if(serverSided && !((ServerCommandSource) source).getMinecraftServer().isDedicated()) {
			serverSided = false;
		}

		//Assume the source is a ServerCommandSource for now
		if(successMessage != null && serverSided) {
			((ServerCommandSource) source).sendFeedback(new LiteralText(successMessage), true);
		} else {
			final String actualName = serverSided ? name : clientName;
			((ServerCommandSource) source).sendFeedback(
					new TranslatableText("commands." + actualName + ".success"), true
			);
		}

		return Command.SINGLE_SUCCESS;
	}
}
