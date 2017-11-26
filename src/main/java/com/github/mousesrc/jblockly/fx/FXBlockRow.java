package com.github.mousesrc.jblockly.fx;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockInput;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.skin.FXBlockRowSkin;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockRow extends Control implements BlockRow, BlockWorkspaceHolder, Connectable{
	
	static enum Type{
		NONE,
		BRANCH,
		INSERT,
		NEXT;
	}
	
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
	private ObjectProperty<Type> type;
	public final Type getType() { 
		Type t = type == null ? Type.NONE : typeProperty().get();
		return t == null ? Type.NONE : t;
	}
	public final void setType(Type type) {typeProperty().set(type);}
	
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
	private ObjectProperty<FXBlock> block;
	public final FXBlock getFXBlock() {return block == null ? null : blockProperty().get();}
	public final void setBlock(FXBlock block) {blockProperty().set(block);}
	
    public final DoubleProperty spacingProperty() {
        if (spacing == null) 
            spacing = new SimpleDoubleProperty(this, "spacing");
        return spacing;
    }
    private DoubleProperty spacing;
    public final void setSpacing(double value) { spacingProperty().set(value); }
    public final double getSpacing() { return spacing == null ? 0 : spacing.get(); }
	
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspacePropertyImpl(){
		if(workspace==null)
			workspace = new ReadOnlyObjectWrapper<FXBlockWorkspace>(this, "workspace");
		return workspace;
	}
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspace;
	public final FXBlockWorkspace getWorkspace() {return workspace == null ? null : workspace.get();}
	public final ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty() {return workspacePropertyImpl().getReadOnlyProperty();}
	private void setWorkspace(FXBlockWorkspace workspace) {workspacePropertyImpl().set(workspace);}
	private final ChangeListener<FXBlockWorkspace> workspaceListener = (observable, oldValue, newValue)->workspacePropertyImpl().set(newValue);
	
	private final ObservableList<Node> components = FXCollections.observableList(new LinkedList<>());
	
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
	
	public ObservableList<Node> getComponents() {
		return components;
	}
	
	public FXBlock getParentBlock(){
		return (FXBlock) getParent();
	}

	@Override
	public Optional<Block> getBlock() {
		return Optional.ofNullable(getFXBlock());
	}

	@Override
	public List<BlockInput<?>> getInputs() {
		return getComponents().stream()
				.filter(node->node instanceof BlockInput<?>)
				.map(node->(BlockInput<?>)node)
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean connect(FXBlock block, Bounds bounds) {
		switch (getType()) {
		case BRANCH:
			
		case INSERT:
		
		case NEXT:
			
		default:
			return false;
		}
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockRowSkin(this);
	}
}
