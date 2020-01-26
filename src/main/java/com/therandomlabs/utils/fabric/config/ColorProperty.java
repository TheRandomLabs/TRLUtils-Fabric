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

import net.minecraft.util.DyeColor;

/**
 * This enum contains Minecraft dye color values that can be used for TRLUtils-Config
 * configuration properties.
 * <p>
 * This enum should be preferred over {@link DyeColor} as the values in this class
 * can be localized in GUIs.
 */
public enum ColorProperty {
	/**
	 * White.
	 */
	WHITE("white"),
	/**
	 * Orange.
	 */
	ORANGE("orange"),
	/**
	 * Magenta.
	 */
	MAGENTA("magenta"),
	/**
	 * Light blue.
	 */
	LIGHT_BLUE("light_blue"),
	/**
	 * Yellow.
	 */
	YELLOW("yellow"),
	/**
	 * Lime.
	 */
	LIME("lime"),
	/**
	 * Pink.
	 */
	PINK("pink"),
	/**
	 * Gray.
	 */
	GRAY("gray"),
	/**
	 * Light gray.
	 */
	LIGHT_GRAY("light_gray"),
	/**
	 * Cyan.
	 */
	CYAN("cyan"),
	/**
	 * '
	 * Purple.
	 */
	PURPLE("purple"),
	/**
	 * Blue.
	 */
	BLUE("blue"),
	/**
	 * Brown.
	 */
	BROWN("brown"),
	/**
	 * Green.
	 */
	GREEN("green"),
	/**
	 * Red.
	 */
	RED("red"),
	/**
	 * Black.
	 */
	BLACK("black");

	private static String translationKeyPrefix = "";

	private final String translationKey;
	private final DyeColor color;

	ColorProperty(String translationKey) {
		this.translationKey = translationKey;
		color = DyeColor.valueOf(name());
	}

	/**
	 * Returns this {@link ColorProperty}'s translation key.
	 *
	 * @return this {@link ColorProperty}'s translation key.
	 */
	@Override
	public String toString() {
		return translationKeyPrefix + translationKey;
	}

	/**
	 * Returns this {@link ColorProperty} as a {@link DyeColor}.
	 *
	 * @return this {@link ColorProperty} as a {@link DyeColor}.
	 */
	public DyeColor get() {
		return color;
	}

	/**
	 * Sets the global translation key prefix for the {@link ColorProperty} values.
	 * This is necessary as TRLUtils-Fabric cannot package its own localizations.
	 *
	 * @param prefix a global translation key prefix.
	 */
	public static void setTranslationKeyPrefix(String prefix) {
		translationKeyPrefix = prefix;
	}
}
