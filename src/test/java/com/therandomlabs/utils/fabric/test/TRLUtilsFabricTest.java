/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018-2019 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
