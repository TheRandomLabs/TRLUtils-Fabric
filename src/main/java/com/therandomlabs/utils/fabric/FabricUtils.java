package com.therandomlabs.utils.fabric;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import com.mojang.bridge.game.GameVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.lang3.ArrayUtils;

public final class FabricUtils {
	public static final FabricLoader LOADER = FabricLoader.getInstance();

	public static final boolean IS_DEOBFUSCATED = LOADER.isDevelopmentEnvironment();
	public static final boolean IS_CLIENT = LOADER.getEnvironmentType() == EnvType.CLIENT;

	public static final Path MC_DIR = LOADER.getGameDirectory().toPath();

	public static final GameVersion MC_VERSION = MinecraftVersion.create();
	public static final String MC_VERSION_STRING = MC_VERSION.getName();
	public static final int MC_VERSION_NUMBER = Integer.parseInt(MC_VERSION_STRING.split("\\.")[1]);

	private FabricUtils() {}

	public static Object toPrimitiveArray(Object[] boxedArray) {
		if(boxedArray instanceof Boolean[]) {
			return ArrayUtils.toPrimitive((Boolean[]) boxedArray);
		}

		if(boxedArray instanceof Byte[]) {
			return ArrayUtils.toPrimitive((Byte[]) boxedArray);
		}

		if(boxedArray instanceof Character[]) {
			return ArrayUtils.toPrimitive((Character[]) boxedArray);
		}

		if(boxedArray instanceof Double[]) {
			return ArrayUtils.toPrimitive((Double[]) boxedArray);
		}

		if(boxedArray instanceof Float[]) {
			return ArrayUtils.toPrimitive((Float[]) boxedArray);
		}

		if(boxedArray instanceof Integer[]) {
			return ArrayUtils.toPrimitive((Integer[]) boxedArray);
		}

		if(boxedArray instanceof Long[]) {
			return ArrayUtils.toPrimitive((Long[]) boxedArray);
		}

		if(boxedArray instanceof Short[]) {
			return ArrayUtils.toPrimitive((Short[]) boxedArray);
		}

		return boxedArray;
	}

	public static Object[] toBoxedArray(Object primitiveArray) {
		if(primitiveArray instanceof Object[]) {
			return (Object[]) primitiveArray;
		}

		if(primitiveArray instanceof boolean[]) {
			return ArrayUtils.toObject((byte[]) primitiveArray);
		}

		if(primitiveArray instanceof byte[]) {
			return ArrayUtils.toObject((byte[]) primitiveArray);
		}

		if(primitiveArray instanceof char[]) {
			return ArrayUtils.toObject((char[]) primitiveArray);
		}

		if(primitiveArray instanceof double[]) {
			return ArrayUtils.toObject((double[]) primitiveArray);
		}

		if(primitiveArray instanceof float[]) {
			return ArrayUtils.toObject((float[]) primitiveArray);
		}

		if(primitiveArray instanceof int[]) {
			return ArrayUtils.toObject((int[]) primitiveArray);
		}

		if(primitiveArray instanceof long[]) {
			return ArrayUtils.toObject((long[]) primitiveArray);
		}

		if(primitiveArray instanceof short[]) {
			return ArrayUtils.toObject((long[]) primitiveArray);
		}

		return (Object[]) primitiveArray;
	}

	public static Path getPath(String path) {
		try {
			return Paths.get(path).normalize();
		} catch(InvalidPathException ignored) {}

		return null;
	}

	public static String toStringWithUnixPathSeparators(Path path) {
		return path.toString().replace('\\', '/');
	}

	public static Field findField(Class<?> clazz, String... names) {
		for(Field field : clazz.getDeclaredFields()) {
			for(String name : names) {
				if(name.equals(field.getName())) {
					field.setAccessible(true);
					return field;
				}
			}
		}

		return null;
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		return findMethod(clazz, name, name, parameterTypes);
	}

	public static Method findMethod(Class<?> clazz, String name, String obfName,
			Class<?>... parameterTypes) {
		for(Method method : clazz.getDeclaredMethods()) {
			final String methodName = method.getName();

			if((name.equals(methodName) || obfName.equals(methodName)) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException ignored) {}

		return null;
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new CrashException(new CrashReport(message, throwable));
	}
}
