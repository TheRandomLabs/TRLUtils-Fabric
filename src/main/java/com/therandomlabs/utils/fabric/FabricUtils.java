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

package com.therandomlabs.utils.fabric;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.mojang.bridge.game.GameVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Contains utility fields and methods that I tend to use in my Fabric mods.
 * There is no coherent theme behind this class.
 */
public final class FabricUtils {
	/**
	 * Contains the value returned by {@link FabricLoader#getInstance()}.
	 */
	public static final FabricLoader LOADER = FabricLoader.getInstance();

	/**
	 * Whether the current environment is a developer environment.
	 *
	 * @see FabricLoader#isDevelopmentEnvironment()
	 */
	public static final boolean IS_DEVELOPMENT_ENVIRONMENT = LOADER.isDevelopmentEnvironment();

	/**
	 * Whether the game is running as a client.
	 *
	 * @see FabricLoader#getEnvironmentType()
	 */
	public static final boolean IS_CLIENT = LOADER.getEnvironmentType() == EnvType.CLIENT;

	/**
	 * The Minecraft game directory.
	 *
	 * @see FabricLoader#getGameDirectory()
	 */
	public static final Path MC_DIRECTORY = LOADER.getGameDirectory().toPath();

	/**
	 * The current Minecraft version as returned by {@link MinecraftVersion#create()}.
	 */
	public static final GameVersion MC_VERSION = MinecraftVersion.create();

	/**
	 * The current Minecraft version string.
	 *
	 * @see GameVersion#getName()
	 */
	public static final String MC_VERSION_STRING = MC_VERSION.getName();

	/**
	 * Returns the current Minecraft major version number.
	 */
	public static final int MC_MAJOR_VERSION =
			Integer.parseInt(Splitter.on('.').splitToList(MC_VERSION_STRING).get(1));

	private FabricUtils() {}

	/**
	 * Returns the specified array as a primitive array.
	 *
	 * @param array a boxed array.
	 * @return the specified array as a primitive array.
	 */
	public static Object toPrimitiveArray(Object[] array) {
		if (array instanceof Boolean[]) {
			return ArrayUtils.toPrimitive((Boolean[]) array);
		}

		if (array instanceof Byte[]) {
			return ArrayUtils.toPrimitive((Byte[]) array);
		}

		if (array instanceof Character[]) {
			return ArrayUtils.toPrimitive((Character[]) array);
		}

		if (array instanceof Double[]) {
			return ArrayUtils.toPrimitive((Double[]) array);
		}

		if (array instanceof Float[]) {
			return ArrayUtils.toPrimitive((Float[]) array);
		}

		if (array instanceof Integer[]) {
			return ArrayUtils.toPrimitive((Integer[]) array);
		}

		if (array instanceof Long[]) {
			return ArrayUtils.toPrimitive((Long[]) array);
		}

		if (array instanceof Short[]) {
			return ArrayUtils.toPrimitive((Short[]) array);
		}

		throw new IllegalArgumentException("array should be a boxed array");
	}

	/**
	 * Returns the specified array as a boxed array.
	 *
	 * @param array a primitive array.
	 * @return the specified array as a boxed array.
	 */
	public static Object[] toBoxedArray(Object array) {
		Preconditions.checkNotNull(array, "array should not be null");

		if (array instanceof Object[]) {
			return (Object[]) array;
		}

		if (array instanceof boolean[]) {
			return ArrayUtils.toObject((boolean[]) array);
		}

		if (array instanceof byte[]) {
			return ArrayUtils.toObject((byte[]) array);
		}

		if (array instanceof char[]) {
			return ArrayUtils.toObject((char[]) array);
		}

		if (array instanceof double[]) {
			return ArrayUtils.toObject((double[]) array);
		}

		if (array instanceof float[]) {
			return ArrayUtils.toObject((float[]) array);
		}

		if (array instanceof int[]) {
			return ArrayUtils.toObject((int[]) array);
		}

		if (array instanceof long[]) {
			return ArrayUtils.toObject((long[]) array);
		}

		if (array instanceof short[]) {
			return ArrayUtils.toObject((short[]) array);
		}

		throw new IllegalArgumentException("array should be an array");
	}

	/**
	 * Returns the specified string as a normalized {@link Path}.
	 *
	 * @param path a path.
	 * @return the specified string as a normalized {@link Path}.
	 */
	public static Path getPath(String path) {
		try {
			return Paths.get(path).normalize();
		} catch (InvalidPathException ignored) {}

		return null;
	}

	/**
	 * Returns the string representation of the specified {@link Path} with Unix directory
	 * separators.
	 *
	 * @param path a {@link Path}.
	 * @return the string representation of the specified {@link Path} with Unix directory
	 * separators.
	 */
	public static String withUnixDirectorySeparators(Path path) {
		Preconditions.checkNotNull(path, "path should not be null");
		return withUnixDirectorySeparators(path.toString());
	}

	/**
	 * Returns the specified path with Unix directory separators.
	 *
	 * @param path a path.
	 * @return the specified path with Unix directory separators.
	 */
	public static String withUnixDirectorySeparators(String path) {
		Preconditions.checkNotNull(path, "path should not be null");
		return path.replace('\\', '/');
	}

	/**
	 * Quietly retrieves the field with any of the specified names in the specified class.
	 *
	 * @param clazz a class.
	 * @param names an array of possible field names.
	 * @return the {@link Field} with any of the specified names, or otherwise {@code null}.
	 */
	@Nullable
	public static Field findField(Class<?> clazz, String... names) {
		Preconditions.checkNotNull(clazz, "clazz should not be null");
		Preconditions.checkNotNull(names, "names should not be null");

		for (Field field : clazz.getDeclaredFields()) {
			for (String name : names) {
				if (name.equals(field.getName())) {
					field.setAccessible(true);
					return field;
				}
			}
		}

		return null;
	}

	/**
	 * Quietly retrieves the method with the specified name and parameter types in the
	 * specified class.
	 *
	 * @param clazz a class.
	 * @param name a method name.
	 * @param parameterTypes an array of parameter types.
	 * @return a {@link Method} that matches the specified parameters, or otherwise {@code null}.
	 */
	@SuppressWarnings("GrazieInspection")
	@Nullable
	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		return findMethod(clazz, name, name, parameterTypes);
	}

	/**
	 * Quietly retrieves the method with the specified name or obfuscated name and parameter types
	 * in the specified class.
	 *
	 * @param clazz a class.
	 * @param name a method name.
	 * @param obfuscatedName an obfuscated method name.
	 * @param parameterTypes an array of parameter types.
	 * @return a {@link Method} that matches the specified parameters, or otherwise {@code null}.
	 */
	@SuppressWarnings("GrazieInspection")
	@Nullable
	public static Method findMethod(
			Class<?> clazz, String name, String obfuscatedName, Class<?>... parameterTypes
	) {
		Preconditions.checkNotNull(clazz, "clazz should not be null");
		Preconditions.checkNotNull(name, "name should not be null");
		Preconditions.checkNotNull(obfuscatedName, "obfuscatedName should not be null");
		Preconditions.checkNotNull(parameterTypes, "parameterTypes should not be null");

		for (Method method : clazz.getDeclaredMethods()) {
			final String methodName = method.getName();

			if ((name.equals(methodName) || obfuscatedName.equals(methodName)) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	/**
	 * Quietly retrieves the class with the specified name.
	 *
	 * @param name a class name.
	 * @return the class with the specified name as returned by {@link Class#forName(String)},
	 * or {@code null} if it cannot be found.
	 */
	@Nullable
	public static Class<?> getClass(String name) {
		Preconditions.checkNotNull(name, "name should not be null");

		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ignored) {}

		return null;
	}

	/**
	 * Returns whether the server of the specified {@link CommandSource} is dedicated.
	 *
	 * @param source a {@link CommandSource}.
	 * @return {@code true} if the server of the specified {@link CommandSource} is dedicated,
	 * or otherwise {@code false}.
	 */
	public static boolean isDedicatedServer(CommandSource source) {
		return source instanceof ServerCommandSource &&
				((ServerCommandSource) source).getMinecraftServer().isDedicated();
	}

	/**
	 * Throws a {@link CrashException} by constructing a {@link CrashReport} with the specified
	 * message and {@link Throwable}.
	 *
	 * @param message a message.
	 * @param throwable a {@link Throwable}.
	 */
	public static void crashReport(String message, Throwable throwable) {
		Preconditions.checkNotNull(message, "message should not be null");
		Preconditions.checkNotNull(throwable, "throwable should not be null");
		throw new CrashException(new CrashReport(message, throwable));
	}
}
