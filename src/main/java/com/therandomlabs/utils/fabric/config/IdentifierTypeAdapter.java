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

package com.therandomlabs.utils.fabric.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.utils.config.TypeAdapter;
import com.therandomlabs.utils.config.TypeAdapters;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("NullAway")
final class IdentifierTypeAdapter implements TypeAdapter {
	private static final Map<Class<?>, MutableRegistry<?>> registries = new HashMap<>();

	private final Class<?> registryEntryClass;
	private final Registry<Object> registry;
	private final boolean isArray;

	static {
		reloadRegistries();
	}

	@SuppressWarnings("unchecked")
	IdentifierTypeAdapter(Class<?> registryEntryClass, boolean isArray) {
		this.registryEntryClass = registryEntryClass;
		registry = (Registry<Object>) registries.get(registryEntryClass);
		this.isArray = isArray;
	}

	@Override
	public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
		if (isArray) {
			return getArrayValue(config, name);
		}

		final String identifierString = config.get(name);

		if (identifierString.isEmpty()) {
			return defaultValue;
		}

		final Identifier identifier =
				new Identifier(identifierString.replaceAll("\\s", ""));
		final Object value = registry.get(identifier);
		return value == null ? defaultValue : value;
	}

	@Override
	public void setValue(CommentedFileConfig config, String name, Object value) {
		if (isArray) {
			config.set(
					name,
					Arrays.stream((Object[]) value).
							map(this::asString).
							collect(Collectors.toList())
			);
		} else {
			config.set(name, asString(value));
		}
	}

	@Override
	public String asString(Object value) {
		return value == null ? "" : registry.getId(value).toString();
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean canBeNull() {
		return true;
	}

	private Object getArrayValue(CommentedFileConfig config, String name) {
		final List<String> list = config.get(name);
		final List<Object> values = new ArrayList<>(list.size());

		for (String element : list) {
			final Object object = registry.get(new Identifier(element.replaceAll("\\s", "")));

			if (object != null) {
				values.add(object);
			}
		}

		return values.toArray((Object[]) Array.newInstance(registryEntryClass, 0));
	}

	static void initialize() {
		TypeAdapters.registerAutoRegistrar(IdentifierTypeAdapter::registerIfRegistryEntry);
	}

	private static void reloadRegistries() {
		registries.clear();

		for (MutableRegistry<?> registry : Registry.REGISTRIES) {
			final Optional<Identifier> identifier = registry.getIds().stream().findAny();

			if (!identifier.isPresent()) {
				continue;
			}

			Class<?> clazz = registry.get(identifier.get()).getClass();

			//We do this so that we get the right base class, e.g. Item instead of AirBlockItem.
			if (clazz.getSuperclass() != Object.class) {
				clazz = clazz.getSuperclass();
			}

			registries.put(clazz, registry);
		}
	}

	private static void registerIfRegistryEntry(Class<?> clazz) {
		if (registries.containsKey(clazz)) {
			register(clazz);
		} else if (clazz.isArray()) {
			final Class<?> componentType = clazz.getComponentType();

			if (registries.containsKey(clazz)) {
				register(componentType);
			}
		}
	}

	private static void register(Class<?> clazz) {
		TypeAdapters.register(clazz, new IdentifierTypeAdapter(clazz, false));
		TypeAdapters.register(
				Array.newInstance(clazz, 0).getClass(),
				new IdentifierTypeAdapter(clazz, true)
		);
	}
}
