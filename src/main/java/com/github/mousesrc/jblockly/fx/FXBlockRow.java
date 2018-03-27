package com.github.mousesrc.jblockly.fx;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockInputer;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.util.FXHelper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
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
		
		public boolean isConnectable(ConnectionType connectionType) {
			switch (this) {
			case BRANCH:
			case NEXT:
				return connectionType == ConnectionType.TOP;
			case INSERT:
				return connectionType == ConnectionType.LEFT;
			default:
				return false;
			}
		}
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
    
    public final ObjectProperty<Insets> componentPaddingProperty(){
		if (componentPadding == null)
			componentPadding = new SimpleObjectProperty<Insets>(this, "componentPadding");
		return componentPadding;
    }
    private ObjectProperty<Insets> componentPadding;
    public final Insets getComponentPadding() {return componentPadding == null ? Insets.EMPTY : componentPadding.get();}
	public final void setComponentPadding(Insets componentPadding) {componentPaddingProperty().set(componentPadding);}
	
	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}
	
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspacePropertyImpl(){
		if(workspace == null)
			workspace = new ReadOnlyObjectWrapper<FXBlockWorkspace>();
		return workspace;
	}
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspace;
	public final ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty() {return workspacePropertyImpl().getReadOnlyProperty();}
	public final FXBlockWorkspace getWorkspace() {return workspace == null ? null : workspace.get();}
	
	private double alignedWidth = 0;
	protected final double getAlignedWidth() {return alignedWidth;}
	protected final void setAlignedWidth(double alignedWidth) {this.alignedWidth = alignedWidth;}
	
	private double componentWidth = 0;
	protected final double getComponentWidth() {return componentWidth;}
	protected final void setComponentWidth(double componentWidth) {this.componentWidth = componentWidth;}
	
	private double componentHeight = 0;
	protected final double getComponentHeight() {return componentHeight;}
	protected final void setComponentHeight(double componentHeight) {this.componentHeight = componentHeight;}
	
	private Bounds connectBounds;
	protected Bounds getConnectBounds() {
		if (connectBounds == null) {
			final double x = getLayoutX(), y = getLayoutY(), alignedRenderWidth = getAlignedWidth(),
					componentWidth = getComponentWidth();
			switch (getType()) {
			case INSERT:
				connectBounds = new BoundingBox(x + alignedRenderWidth, y + FXBlockConstant.LEFT_OFFSET_Y,
						FXBlockConstant.LEFT_WIDTH, FXBlockConstant.LEFT_HEIGHT);
				break;
			case BRANCH:
				connectBounds = new BoundingBox(x + componentWidth + FXBlockConstant.TOP_OFFSET_X, y,
						FXBlockConstant.TOP_WIDTH, FXBlockConstant.TOP_HEIGHT);
				break;
			case NEXT:
				connectBounds = new BoundingBox(x + FXBlockConstant.TOP_OFFSET_X, y, FXBlockConstant.TOP_WIDTH,
						FXBlockConstant.TOP_HEIGHT);
				break;
			case NONE:
				break;
			}
		}
		return connectBounds;
	}
	
	private final ObservableList<Node> components = FXCollections.observableList(new LinkedList<>());
	
	private static final String DEFAULT_STYLE_CLASS = "block-row";
	
	public FXBlockRow() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		initBlockChangeListener();
		
		setSpacing(5);
		setComponentPadding(new Insets(5, 5, 0, 5));
	}
	
	private void initWorkspaceListener(){
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(newValue instanceof BlockWorkspaceHolder)
				workspacePropertyImpl().bind(((BlockWorkspaceHolder)newValue).workspaceProperty());
			else
				workspacePropertyImpl().unbind();
		});
	}
	
	private void initBlockChangeListener() {
		blockProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != null) {
				oldValue.addToWorkspace(10, 10);
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
	
	protected double computeRenderWidth() {
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
	
	public boolean isConnectable(FXBlock block) {
		return getType().isConnectable(block.getConnectionType());
	}
	
	@Override
	public ConnectionResult connect(FXBlock block, Bounds bounds) {
		if(getType() == Type.NONE)
			return ConnectionResult.FAILURE;
		if(getConnectBounds().intersects(bounds)) {
			if(!isConnectable(block))
				return ConnectionResult.CANCELLED;
			setBlock(block);
			return ConnectionResult.SUCCESSFUL;
		}
		FXBlock child = getFXBlock();
		if(child == null)
			return ConnectionResult.FAILURE;
		return child.connect(block, FXHelper.subtractBounds2D(bounds, this.getLayoutX(), this.getLayoutY()));
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
	
	protected double getNextRowAlignedRenderWidth() {
		ObservableList<FXBlockRow> rows = getParentBlock().getFXRows();
		int nextIndex = rows.indexOf(this) + 1;
		return nextIndex < rows.size() ? rows.get(nextIndex).getAlignedWidth() : getAlignedWidth();
	}
	
	protected double getBlockHeight(){
		FXBlock block = getFXBlock();
		return block == null ? 0 : block.getHeight();
	}
}
