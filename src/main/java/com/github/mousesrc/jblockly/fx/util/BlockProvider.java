package com.github.mousesrc.jblockly.fx.util;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.mousesrc.jblockly.fx.FXBlock;

public interface BlockProvider {
	
	String getName();
	
	FXBlock create();
	
	Map<String, Object> getProperties();

	Set<String> getPropertyKeys();

	<V> Optional<V> getProperty(String key);

	<V> Optional<V> getProperty(String key, Class<V> type);

	<V> void addProperty(String key, V value);

	void removeProperty(String key);
	
	boolean containsProperty(String key);

}
