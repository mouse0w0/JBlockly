package com.github.mousesrc.jblockly.api;

import java.util.List;

public interface Block {
	
	String getBlockName();
	
	List<BlockRow> getRows();
	
}
