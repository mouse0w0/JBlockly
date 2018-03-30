package com.github.mousesrc.jblockly.fx.input;

public interface Inputer<T> {

	T getValue();
	
	void setValue(T value);
	
	default boolean hasValue() {
		return getValue() != null;
	}
}
