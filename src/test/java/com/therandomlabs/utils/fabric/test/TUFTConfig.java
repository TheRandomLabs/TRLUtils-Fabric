package com.therandomlabs.utils.fabric.test;

import com.therandomlabs.utils.config.Config;
import net.minecraft.item.Item;

@Config(id = TRLUtilsFabricTest.MOD_ID, comment = "TRLUtils-Fabric Test configuration")
public final class TUFTConfig {
	public static final class Test {
		@Config.Blacklist("minecraft:air")
		@Config.Property("The registry name of the test item.")
		public static Item testItem;
	}

	@Config.Category("Test options.")
	public static final Test test = null;
}
