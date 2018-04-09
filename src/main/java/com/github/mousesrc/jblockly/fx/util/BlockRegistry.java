package com.github.mousesrc.jblockly.fx.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockRegistry {
	
	private BlockRegistry parent;
	private final Map<String, BlockProvider> registeredBlocks = new HashMap<>();
	
	public BlockRegistry() {}
	
	public BlockRegistry(BlockRegistry parent) {
		this.setParent(parent);
	}
	
	public void register(BlockProvider provider) {
		if(get(provider.getName()) != null)
			throw new IllegalArgumentException();
		registeredBlocks.put(provider.getName(), provider);
	}
	
	public void unregister(BlockProvider provider) {
		registeredBlocks.remove(provider.getName());
	}
	
	public BlockProvider get(String name) {
		BlockProvider provider = registeredBlocks.get(name);
		return provider != null ? provider : parent != null ? parent.get(name) : null;
	}
	
	public Collection<BlockProvider> getRegisteredBlocks() {
		return registeredBlocks.values();
	}

	public BlockRegistry getParent() {
		return parent;
	}

	public void setParent(BlockRegistry parent) {
		this.parent = parent;
	}
}
