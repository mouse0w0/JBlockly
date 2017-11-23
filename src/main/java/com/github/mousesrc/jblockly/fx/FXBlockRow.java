package com.github.mousesrc.jblockly.fx;

import java.util.Optional;
import java.util.Set;

import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockInput;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.skin.FXBlockRowSkin;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockRow extends Control implements BlockRow{
	
	static enum Type{
		NONE,
		BRANCH,
		INSERT,
		NEXT;
	}
	
	public Type getType(){
		return null;
	}

	@Override
	public Optional<Block> getBlock() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public Set<BlockInput<?>> getInputers() {
		// TODO 自动生成的方法存根
		return null;
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockRowSkin(this);
	}

}
