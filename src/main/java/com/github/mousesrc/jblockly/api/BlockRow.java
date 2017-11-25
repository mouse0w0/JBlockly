package com.github.mousesrc.jblockly.api;

import java.util.List;
import java.util.Optional;

public interface BlockRow {

	Optional<Block> getBlock();
	
	List<BlockInput<?>> getInputs();
}
