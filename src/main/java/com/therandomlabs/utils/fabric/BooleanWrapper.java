package com.therandomlabs.utils.fabric;

public final class BooleanWrapper {
	private boolean value;

	public BooleanWrapper(boolean value) {
		this.value = value;
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}
}
