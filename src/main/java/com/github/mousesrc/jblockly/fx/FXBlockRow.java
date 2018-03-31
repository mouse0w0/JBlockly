package com.github.mousesrc.jblockly.fx;

import java.util.LinkedList;

import com.github.mousesrc.jblockly.fx.input.Inputer;
import com.github.mousesrc.jblockly.fx.util.FXHelper;
import com.github.mousesrc.jblockly.model.BlockRow;

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

public class FXBlockRow extends Control implements BlockWorkspaceHolder, Connectable{
    
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
			type = new SimpleObjectProperty<Type>(this, "type", Type.NONE){
				@Override
				public void set(Type newValue) {
					super.set(newValue == null ? Type.NONE : newValue);
				}
				
				@Override
				protected void invalidated() {
					needUpdateConnectBounds = true;
					requestLayout();
				}
			};
		return type;
	}
	private ObjectProperty<Type> type;
	public final Type getType() { return type == null ? Type.NONE : type.get();}
	public final void setType(Type type) {typeProperty().set(type);}
	
	public final ObjectProperty<FXBlock> blockProperty() {
		if(block == null)
			block = new SimpleObjectProperty<FXBlock>(this, "block");
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
	private boolean needUpdateConnectBounds = true;
	protected Bounds getConnectBounds() {
		if (needUpdateConnectBounds) {
			connectBounds = computeConnectBounds();
			needUpdateConnectBounds = false;
		}
		return connectBounds;
	}
	protected Bounds computeConnectBounds() {
		final double x = getLayoutX(), y = getLayoutY(), alignedRenderWidth = getAlignedWidth(),
				componentWidth = getComponentWidth();
		switch (getType()) {
		case INSERT:
			return new BoundingBox(x + alignedRenderWidth - FXBlockConstant.LEFT_WIDTH, y + FXBlockConstant.LEFT_OFFSET_Y,
					FXBlockConstant.LEFT_WIDTH, FXBlockConstant.LEFT_HEIGHT);
		case BRANCH:
			return new BoundingBox(x + componentWidth + FXBlockConstant.TOP_OFFSET_X, y,
					FXBlockConstant.TOP_WIDTH, FXBlockConstant.TOP_HEIGHT);
		case NEXT:
			return new BoundingBox(x + FXBlockConstant.TOP_OFFSET_X, y, FXBlockConstant.TOP_WIDTH,
					FXBlockConstant.TOP_HEIGHT);
		default:
			return null;
		}
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
		return block == null ? 0 : snapSize(block.prefHeight(-1));
	}
	
	@Override
	public void relocate(double x, double y) {
		super.relocate(x, y);
		needUpdateConnectBounds = true;
	}
	
	public BlockRow toModel() {
		BlockRow row = new BlockRow();
		FXBlock block = getFXBlock();
		if (block != null)
			row.setBlock(block.toModel());
		for (Node node : getComponents()) {
			if (node instanceof Inputer<?>) {
				Inputer<?> inputer = (Inputer<?>) node;
				row.addData(inputer.getName(), inputer.getValue());
			}
		}
		return row;
	}
}
