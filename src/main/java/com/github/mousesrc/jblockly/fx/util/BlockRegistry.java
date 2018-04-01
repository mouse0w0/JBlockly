package com.github.mousesrc.jblockly.fx.util;

import java.util.Collection;
import java.util.Map;

public class BlockRegistry {
	
	private BlockRegistry parent;
	private Map<String, BlockProvider> registeredBlocks;
	
	public BlockRegistry() {}
	
	public BlockRegistry(BlockRegistry parent) {
		this.setParent(parent);
	}
	
	public void register(BlockProvider factory) {
		if(get(factory.getName()) != null)
			throw new IllegalArgumentException();
		registeredBlocks.put(factory.getName(), factory);
	}
	
	public void unregister(BlockProvider factory) {
		registeredBlocks.remove(factory.getName());
	}
	
	public BlockProvider get(String name) {
		BlockProvider factory = registeredBlocks.get(name);
		return factory != null ? factory : parent != null ? parent.get(name) : null;
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
