package com.github.mousesrc.jblockly.fx;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockInputer;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.util.SVGBuilder;

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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockRow extends Control implements BlockRow, BlockWorkspaceHolder, Connectable{
    
	public static enum Type{
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
            spacing = new SimpleDoubleProperty(this, "spacing"){
        		@Override
        		protected void invalidated() {
        			requestLayout();
        		}
        	};
        return spacing;
    }
    private DoubleProperty spacing;
    public final void setSpacing(double value) { spacingProperty().set(value); }
    public final double getSpacing() { return spacing == null ? 0 : spacing.get(); }
    
    public final ObjectProperty<Insets> componentPaddingProperty(){
		if (componentPadding == null)
			componentPadding = new SimpleObjectProperty<Insets>(this, "componentPadding") {
				@Override
				public void set(Insets newValue) {
					super.set(newValue == null ? Insets.EMPTY : newValue);
				}
				
				@Override
				protected void invalidated() {
					requestLayout();
				}
			};
		return componentPadding;
    }
    private ObjectProperty<Insets> componentPadding;
    public final Insets getComponentPadding() {return componentPadding == null ? Insets.EMPTY : componentPadding.get();}
	public final void setComponentPadding(Insets componentPadding) {componentPaddingProperty().set(componentPadding);}
	
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspacePropertyImpl(){
		if(workspace == null)
			workspace = new ReadOnlyObjectWrapper<FXBlockWorkspace>();
		return workspace;
	}
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspace;
	public final ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty() {return workspacePropertyImpl().getReadOnlyProperty();}
	public final FXBlockWorkspace getWorkspace() {return workspace == null ? null : workspace.get();}
	private void setWorkspace(FXBlockWorkspace workspace) {workspacePropertyImpl().set(workspace);}
	private final ChangeListener<FXBlockWorkspace> workspaceListener = (observable, oldValue, newValue)->workspacePropertyImpl().set(newValue);
	
	private double alignedWidth = 0;
	protected final double getAlignedWidth() {return alignedWidth;}
	protected final void setAlignedWidth(double alignedWidth) {this.alignedWidth = alignedWidth;}
	
	private double componentWidth = 0;
	protected final double getComponentWidth() {return componentWidth;}
	protected final void setComponentWidth(double componentWidth) {this.componentWidth = componentWidth;}
	
	private double componentHeight = 0;
	protected final double getComponentHeight() {return componentHeight;}
	protected final void setComponentHeight(double componentHeight) {this.componentHeight = componentHeight;}
	
	private final ObservableList<Node> components = FXCollections.observableList(new LinkedList<>());
	
	private static final String DEFAULT_STYLE_CLASS = "block-row";
	
	public FXBlockRow() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		
		setSnapToPixel(true);
		
		setMinSize(150, 35);
		setSpacing(5);
		setComponentPadding(new Insets(5, 5, 0, 5));
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
	
	public FXBlock getParentBlock() {
		return (FXBlock) getParent();
	}

	public boolean isFirst(){
		return getParentBlock().getFXRows().indexOf(this) == 0;
	}
	
	public boolean isLast(){
		ObservableList<FXBlockRow> rows = getParentBlock().getFXRows();
		return rows.indexOf(this) == rows.size() - 1;
	}
	
	public void render(SVGBuilder svgBuilder){
		final double x = getLayoutX(), y = getLayoutY(), alignedRenderWidth = getAlignedWidth(),
				componentWidth = getComponentWidth(), componentHeight = getComponentHeight();
		switch (getType()) {
		case INSERT:
			svgBuilder.v(y + FXBlockConstant.LEFT_OFFSET_Y)
					.h(alignedRenderWidth - FXBlockConstant.LEFT_WIDTH)
					.v(y + FXBlockConstant.LEFT_OFFSET_Y + FXBlockConstant.LEFT_HEIGHT)
					.h(alignedRenderWidth);
			break;
		case BRANCH:
			svgBuilder.v(y)
					.h(x + componentWidth + FXBlockConstant.TOP_OFFSET_X + FXBlockConstant.TOP_WIDTH)
					.v(y + FXBlockConstant.TOP_HEIGHT)
					.h(x + componentWidth + FXBlockConstant.TOP_OFFSET_X)
					.v(y)
					.h(x + componentWidth)
					.v(y + Math.max(componentHeight, getBlockHeight()))
					.h(getNextRowAlignedRenderWidth());
			break;
		case NEXT:
			svgBuilder.v(y)
					.h(x + FXBlockConstant.TOP_OFFSET_X + FXBlockConstant.TOP_WIDTH)
					.v(y + FXBlockConstant.TOP_HEIGHT)
					.h(x + FXBlockConstant.TOP_OFFSET_X)
					.v(y);
			break;
		case NONE:
			svgBuilder.v(y + componentHeight);
			break;
		}
	}
	
	double computeRenderWidth() {
		switch (getType()) {
		case BRANCH:
			return getComponentWidth() + FXBlockConstant.BRANCH_ROW_SLOT_MIN_WIDTH;
		case INSERT:
			return getComponentWidth() + FXBlockConstant.LEFT_WIDTH;
		case NONE:
			return getComponentWidth();
		case NEXT:
			return FXBlockConstant.BLOCK_ROW_MIN_WIDTH;
		default:
			return FXBlockConstant.BLOCK_ROW_MIN_WIDTH;
		}
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
	public Optional<Block> getBlock() {
		return Optional.ofNullable(getFXBlock());
	}

	@Override
	public List<BlockInputer<?>> getInputers() {
		return getComponents().stream()
				.filter(node->node instanceof BlockInputer<?>)
				.map(node->(BlockInputer<?>)node)
				.collect(Collectors.toList());
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockRowSkin(this);
	}
	
	private double getNextRowAlignedRenderWidth() {
		ObservableList<FXBlockRow> rows = getParentBlock().getFXRows();
		int nextIndex = rows.indexOf(this) + 1;
		return nextIndex < rows.size() ? rows.get(nextIndex).getAlignedWidth() : getAlignedWidth();
	}
	
	private double getBlockHeight(){
		FXBlock block = getFXBlock();
		return block == null ? 0 : block.getHeight();
	}
}
