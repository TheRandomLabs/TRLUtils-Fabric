package com.therandomlabs.utils.fabric.test;

import com.mojang.brigadier.CommandDispatcher;
import com.therandomlabs.utils.config.ConfigManager;
import com.therandomlabs.utils.fabric.config.ConfigReloadCommand;
import com.therandomlabs.utils.fabric.config.FabricConfig;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;

public final class TRLUtilsFabricTest implements ModInitializer, ClientCommandPlugin {
	public static final String MOD_ID = "trlutils-fabric-test";

	private static final ConfigReloadCommand configReloadCommand =
			new ConfigReloadCommand("tuftreload", "tuftreloadclient", TUFTConfig.class).
					serverSuccessMessage("TRLUtils-Fabric Test configuration reloaded!");

	@Override
	public void onInitialize() {
		FabricConfig.initialize();
		ConfigManager.register(TUFTConfig.class);
		CommandRegistry.INSTANCE.register(false, configReloadCommand::registerServer);
	}

	@Override
	public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
		configReloadCommand.registerClient(commandDispatcher);
	}
}
