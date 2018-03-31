package com.github.mousesrc.jblockly.fx.input;

public interface Inputer<T> {
	
	String getName();
	
	void setName(String name);
	
	T getValue();
	
	void setValue(T value);
	
	default boolean hasValue() {
		return getValue() != null;
	}
}
