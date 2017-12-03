package com.github.mousesrc.jblockly.fx;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockInput;
import com.github.mousesrc.jblockly.api.BlockRow;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockRow extends Control implements BlockRow, BlockWorkspaceHolder, Connectable{
	
	public static final double UNALINGED = 0;
	
	private static final String MARGIN_CONSTRAINT = "block-row-margin";
	
    private static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    private static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            Object value = node.getProperties().get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    public static void setMargin(Node child, Insets value) {
        setConstraint(child, MARGIN_CONSTRAINT, value);
    }

    public static Insets getMargin(Node child) {
        return (Insets)getConstraint(child, MARGIN_CONSTRAINT);
    }

    public static void clearConstraints(Node child) {
        setMargin(child, null);
    }
    
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
	
	DoubleProperty alignedWidthRenderProperty(){
		if(alignedRenderWidth == null)
			alignedRenderWidth = new SimpleDoubleProperty(){
				@Override
				public void set(double newValue) {
					super.set(newValue < 0.0 ? UNALINGED : newValue);
				}
			};
		return alignedRenderWidth;
	}
	private DoubleProperty alignedRenderWidth;
	double getAlignedRenderWidth() {return alignedRenderWidth == null ? UNALINGED : alignedRenderWidth.get();}
	void setAlignedRenderWidth(double alignedWidth) {alignedWidthRenderProperty().set(alignedWidth);}
	
	ReadOnlyObjectWrapper<Bounds> componentBoundsPropertyImpl() {
		if(componentBounds == null)
			componentBounds = new ReadOnlyObjectWrapper<>(new BoundingBox(0, 0, 0, 0));
		return componentBounds;
	}
	private ReadOnlyObjectWrapper<Bounds> componentBounds;
	public final ReadOnlyObjectProperty<Bounds> componentBoundsProperty() {return componentBoundsPropertyImpl().getReadOnlyProperty();}
	public final Bounds getComponentBounds() {return componentBoundsPropertyImpl().get();}
	void setComponentBoundsProperty(Bounds componentBounds){componentBoundsPropertyImpl().set(componentBounds);}
	
	private final ObservableList<Node> components = FXCollections.observableList(new LinkedList<>());
	
	private static final String DEFAULT_STYLE_CLASS = "block-row";
	
	public FXBlockRow() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		
		setSnapToPixel(true);
		
		setMinSize(150, 35);
		setSpacing(5);
		setComponentPadding(new Insets(0, 5, 0, 5));
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
	
	public String render(){
		double x = getLayoutX();
		double y = getLayoutY();
		double alignedRenderWidth = getAlignedRenderWidth();
		switch (getType()) {
		case INSERT:
			return new StringBuilder()
					.append(" V ").append(y + FXBlockConstant.LEFT_OFFSET_Y)
					.append(" H ").append(alignedRenderWidth - FXBlockConstant.LEFT_WIDTH)
					.append(" V ").append(y + FXBlockConstant.LEFT_OFFSET_Y + FXBlockConstant.LEFT_HEIGHT)
					.append(" H ").append(alignedRenderWidth)
					.toString();
		case BRANCH:
			Bounds componentBounds = getComponentBounds();
			return new StringBuilder()
					.append(" V ").append(y)
					.append(" H ").append(x + componentBounds.getWidth() + FXBlockConstant.TOP_OFFSET_X + FXBlockConstant.TOP_WIDTH)
					.append(" V ").append(y + FXBlockConstant.TOP_HEIGHT)
					.append(" H ").append(x + componentBounds.getWidth() + FXBlockConstant.TOP_OFFSET_X)
					.append(" V ").append(y)
					.append(" H ").append(x + componentBounds.getWidth())
					.append(" V ").append(Math.max(componentBounds.getHeight(), getBlockHeight()))
					.append(" H ").append(getNextRowAlignedRenderWidth())
					.toString();
		case NEXT:
			return new StringBuilder()
					.append(" V ").append(y)
					.append(" H ").append(x + FXBlockConstant.TOP_OFFSET_X + FXBlockConstant.TOP_WIDTH)
					.append(" V ").append(y + FXBlockConstant.TOP_HEIGHT)
					.append(" H ").append(x + FXBlockConstant.TOP_OFFSET_X)
					.append(" V ").append(y)
					.toString();
		default:
			return " V " + (y + getHeight());
		}
	}
	
	double computeRenderWidth() {
		switch (getType()) {
		case BRANCH:
			return componentBounds.get().getWidth() + FXBlockConstant.BRANCH_ROW_SLOT_MIN_WIDTH;
		case INSERT:
			return componentBounds.get().getWidth() + FXBlockConstant.LEFT_WIDTH;
		case NONE:
			return componentBounds.get().getWidth();
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
	public List<BlockInput<?>> getInputs() {
		return getComponents().stream()
				.filter(node->node instanceof BlockInput<?>)
				.map(node->(BlockInput<?>)node)
				.collect(Collectors.toList());
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockRowSkin(this);
	}
	
	private double getNextRowAlignedRenderWidth() {
		ObservableList<FXBlockRow> rows = getParentBlock().getFXRows();
		int nextIndex = rows.indexOf(this) + 1;
		return nextIndex < rows.size() ? rows.get(nextIndex).getAlignedRenderWidth() : getAlignedRenderWidth();
	}
	
	private double getBlockHeight(){
		FXBlock block = getFXBlock();
		return block == null ? 0 : block.getHeight();
	}
}
