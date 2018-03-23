package com.github.mousesrc.jblockly.fx.util;

import com.github.mousesrc.jblockly.fx.FXBlock;

public interface BlockFactory {
	
	String getName();
	
	String getGroup();
	
	FXBlock create();

}
