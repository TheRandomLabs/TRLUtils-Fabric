package com.therandomlabs.utils.fabric.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.therandomlabs.utils.config.TypeAdapter;
import com.therandomlabs.utils.config.TypeAdapters;
import com.therandomlabs.utils.fabric.FabricUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public final class IdentifierTypeAdapter implements TypeAdapter {
	private static final Field DEFAULT_ENTRIES =
			FabricUtils.findField(Registry.class, "DEFAULT_ENTRIES", "field_11140");

	private static Map<Class<?>, MutableRegistry<?>> registries = new HashMap<>();

	static {
		reloadRegistries();
	}

	private final Class<?> registryEntryClass;
	private final Registry<Object> registry;
	private final boolean isArray;

	@SuppressWarnings("unchecked")
	public IdentifierTypeAdapter(Class<?> registryEntryClass, boolean isArray) {
		this.registryEntryClass = registryEntryClass;
		registry = (Registry<Object>) registries.get(registryEntryClass);
		this.isArray = isArray;
	}

	@Override
	public Object getValue(CommentedFileConfig config, String name, Object defaultValue) {
		if(!isArray) {
			final String identifierString = config.get(name);

			if(identifierString.isEmpty()) {
				return defaultValue;
			}

			final Identifier identifier =
					new Identifier(identifierString.replaceAll("\\s", ""));
			return registry.containsId(identifier) ? registry.get(identifier) : defaultValue;
		}

		final List<String> list = config.get(name);
		final List<Object> values = new ArrayList<>(list.size());

		for(String element : list) {
			final Object object = registry.get(new Identifier(element.replaceAll("\\s", "")));

			if(object != null) {
				values.add(object);
			}
		}

		return values.toArray((Object[]) Array.newInstance(registryEntryClass, 0));
	}

	@Override
	public void setValue(CommentedFileConfig config, String name, Object value) {
		if(isArray) {
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

	@SuppressWarnings("unchecked")
	public static void reloadRegistries() {
		Map<Identifier, Supplier<?>> defaultEntries = null;

		try {
			defaultEntries = (Map<Identifier, Supplier<?>>) DEFAULT_ENTRIES.get(null);
		} catch(IllegalAccessException ex) {
			FabricUtils.crashReport("Failed to reload registries", ex);
		}

		for(Identifier registryID : Registry.REGISTRIES.getIds()) {
			registries.put(
					defaultEntries.get(registryID).get().getClass(),
					Registry.REGISTRIES.get(registryID)
			);
		}
	}

	public static void registerIfRegistryEntry(Class<?> clazz) {
		if(registries.containsKey(clazz)) {
			register(clazz);
		} else if(clazz.isArray()) {
			final Class<?> componentType = clazz.getComponentType();

			if(registries.containsKey(clazz)) {
				register(componentType);
			}
		}
	}

	static void initialize() {
		TypeAdapters.registerAutoRegistrar(IdentifierTypeAdapter::registerIfRegistryEntry);
	}

	private static void register(Class<?> clazz) {
		TypeAdapters.register(clazz, new IdentifierTypeAdapter(clazz, false));
		TypeAdapters.register(
				Array.newInstance(clazz, 0).getClass(),
				new IdentifierTypeAdapter(clazz, true)
		);
	}
}
