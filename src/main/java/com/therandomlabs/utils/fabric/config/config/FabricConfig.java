package com.therandomlabs.utils.fabric.config.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import com.therandomlabs.utils.config.ConfigManager;
import com.therandomlabs.utils.fabric.config.FabricUtils;

public final class FabricConfig {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface MCVersion {
		//Version range
		String value();
	}

	private FabricConfig() {}

	public static void initialize() {
		ConfigManager.setClient(FabricUtils.IS_CLIENT);
		ConfigManager.registerVersionChecker(FabricConfig::testVersionRange);
		IdentifierTypeAdapter.initialize();
	}

	private static boolean testVersionRange(Field field) {
		final MCVersion mcVersion = field.getAnnotation(MCVersion.class);

		if(mcVersion == null) {
			return true;
		}

		final String versionRange = mcVersion.value().trim();

		if(versionRange.isEmpty()) {
			throw new IllegalArgumentException("Version range must not be empty");
		}

		//TODO
		/*final VersionRange range = MavenVersionAdapter.createFromVersionSpec(versionRange);

		if(!range.containsVersion(ForgeUtils.MC_ARTIFACT_VERSION)) {
			return false;
		}*/

		return true;
	}
}
