package com.github.mousesrc.jblockly.fx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.github.mousesrc.jblockly.fx.FXBlock;

public abstract class BlockProviderBase implements BlockProvider{

	private final String name;
	private final Map<String, Object> properties = new HashMap<>();
	
	public BlockProviderBase(String name) {
		this.name = Objects.requireNonNull(name);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public Set<String> getPropertyKeys() {
		return getProperties().keySet();
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getProperty(String key) {
		return Optional.ofNullable((V)getProperties().get(key));
	}

	@SuppressWarnings("unchecked")
	public <V> Optional<V> getProperty(String key, Class<V> type) {
		Object value = getProperties().get(key);
		return type.isAssignableFrom(value.getClass()) ? Optional.of((V) value) : Optional.empty();
	}

	public <V> void addProperty(String key, V value) {
		getProperties().put(key, value);
	}

	public void removeProperty(String key) {
		getProperties().remove(key);
	}

	public boolean containsProperty(String key) {
		return getProperties().containsKey(key);
	}
	
	protected FXBlock preCreate() {
		FXBlock block = new FXBlock();
		block.setName(getName());
		return block;
	}
}
