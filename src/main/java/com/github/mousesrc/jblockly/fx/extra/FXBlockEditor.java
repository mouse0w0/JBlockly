package com.github.mousesrc.jblockly.fx.extra;

import com.github.mousesrc.jblockly.fx.FXBlockWorkspace;
import com.github.mousesrc.jblockly.fx.extra.skin.FXBlockEditorSkin;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockEditor extends Control {
	
	private FXBlockWorkspace workspace;
	public final FXBlockWorkspace getWorkspace() {
		return workspace;
	}
	
	public FXBlockEditor() {
		getStyleClass().setAll("block-editor");
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockEditorSkin(this);
	}

}
