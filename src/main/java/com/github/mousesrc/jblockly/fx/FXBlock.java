package com.github.mousesrc.jblockly.fx;

import java.util.Set;

import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.skin.FXBlockSkin;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlock extends Control implements Block{
	
	static enum ConnectionType{
		NONE,
		TOP,
		BOTTOM,
		TOP_AND_BOTTOM,
		LEFT;
	}

	@Override
	public Set<BlockRow> getRows() {
		// TODO 自动生成的方法存根
		return null;
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockSkin(this);
	}

}
