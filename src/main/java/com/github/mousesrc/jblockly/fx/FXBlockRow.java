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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class FXBlockRow extends Control implements BlockRow, BlockWorkspaceHolder, Connectable{
	
	public static final double UNALINGED = Double.NaN;
	
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
	
	DoubleProperty alignedWidthProperty(){
		if(alignedWidth == null)
			alignedWidth = new SimpleDoubleProperty(){
				@Override
				public void set(double newValue) {
					super.set(newValue <= 0.0 ? UNALINGED : newValue);
				}
			};
		return alignedWidth;
	}
	private DoubleProperty alignedWidth;
	public final double getAlignedWidth() {return alignedWidth == null ? UNALINGED : alignedWidth.get();}
	void setAlignedWidth(double alignedWidth) {alignedWidthProperty().set(alignedWidth);}
	
	private ReadOnlyObjectWrapper<Bounds> componentBoundsPropertyImpl() {
		if(componentBounds == null)
			componentBounds = new ReadOnlyObjectWrapper<>();
		return componentBounds;
	}
	private ReadOnlyObjectWrapper<Bounds> componentBounds;
	public final ReadOnlyObjectProperty<Bounds> componentBoundsProperty() {return componentBoundsPropertyImpl().getReadOnlyProperty();}
	public final Bounds getComponentBounds() {return componentBoundsPropertyImpl().get();}
	
	private final ObservableList<Node> components = FXCollections.observableList(new LinkedList<>());
	
	private static final String DEFAULT_STYLE_CLASS = "block-row";
	
	public FXBlockRow() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		
		setSnapToPixel(true);
		
		setMinSize(150, 35);
		setComponentPadding(new Insets(5, 5, 0, 10));
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
		FXBlockRowSkin skin = new FXBlockRowSkin(this);
		skin.setComponentBoundsWrapper(componentBoundsPropertyImpl());
		return skin;
	}
}
