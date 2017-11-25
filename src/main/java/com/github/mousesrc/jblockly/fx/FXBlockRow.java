package com.github.mousesrc.jblockly.fx;

import java.util.List;
import java.util.Optional;
import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockInput;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.skin.FXBlockRowSkin;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockRow extends Control implements BlockRow,BlockWorkspaceHolder,Connectable{
	
	static enum Type{
		NONE,
		BRANCH,
		INSERT,
		NEXT;
	}
	
	private ObjectProperty<FXBlock> block;
	public final ObjectProperty<FXBlock> blockProperty() {
		if(block == null)
			block = new SimpleObjectProperty<FXBlock>(this, "block"){
				@Override
				protected void invalidated() {
					requestLayout();
				}
			};
		return block;
	}
	public final FXBlock getFXBlock() {return block == null ? null : blockProperty().get();}
	public final void setBlock(FXBlock block) {blockProperty().set(block);}
	
	private ObjectProperty<Type> type;
	public final ObjectProperty<Type> typeProperty() {
		if(type == null)
			type = new SimpleObjectProperty<Type>(this, "type"){
				@Override
				protected void invalidated() {
					requestLayout();
				}
			};
		return type;
	}
	public final Type getType() { 
		Type t = type == null ? Type.NONE : typeProperty().get();
		return t == null ? Type.NONE : t;
	}
	public final void setType(Type type) {typeProperty().set(type);}
	
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspace;
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspacePropertyImpl(){
		if(workspace==null)
			workspace = new ReadOnlyObjectWrapper<FXBlockWorkspace>(this, "workspace");
		return workspace;
	}
	public final FXBlockWorkspace getWorkspace() {return workspace == null ? null : workspace.get();}
	public final ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty() {return workspacePropertyImpl().getReadOnlyProperty();}
	private void setWorkspace(FXBlockWorkspace workspace) {workspacePropertyImpl().set(workspace);}
	private final ChangeListener<FXBlockWorkspace> workspaceListener = (observable, oldValue, newValue)->workspacePropertyImpl().set(newValue);
	
	private static final String DEFAULT_STYLE_CLASS = "block-row";
	
	public FXBlockRow() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		
		setMinSize(100, 30);
	}
	
	private void initWorkspaceListener(){
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(oldValue instanceof BlockWorkspaceHolder)
				((BlockWorkspaceHolder)oldValue).workspaceProperty().removeListener(workspaceListener);
			if(newValue instanceof BlockWorkspaceHolder){
				BlockWorkspaceHolder holder = (BlockWorkspaceHolder)newValue;
				setWorkspace(holder.workspaceProperty().get());
				holder.workspaceProperty().addListener(workspaceListener);
			}
		});
	}

	@Override
	public Optional<Block> getBlock() {
		return Optional.ofNullable(getFXBlock());
	}

	@Override
	public List<BlockInput<?>> getInputs() {
		// TODO 自动生成的方法存根
		return null;
	}
	
	@Override
	public boolean connect(FXBlock block, Point2D point) {
		// TODO 自动生成的方法存根
		return false;
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockRowSkin(this);
	}
}
