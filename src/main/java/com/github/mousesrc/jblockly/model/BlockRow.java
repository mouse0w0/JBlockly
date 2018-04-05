package com.github.mousesrc.jblockly.model;

import java.util.Optional;
import com.github.mousesrc.jblockly.util.DataContainer;

public class BlockRow {
	
	private Block block;
	private final DataContainer dataContainer = new DataContainer();

	public Optional<Block> getBlock() {
		return Optional.ofNullable(block);
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public boolean hasBlock() {
		return block != null;
	}

	public DataContainer getData() {
		return dataContainer;
	}
}
