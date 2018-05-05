package com.github.mousesrc.jblockly.fx;

import java.util.LinkedList;
import com.github.mousesrc.jblockly.fx.util.FXHelper;
import com.github.mousesrc.jblockly.model.Block;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import static com.github.mousesrc.jblockly.fx.FXBlockConstant.*;

public class FXBlock extends Control implements BlockWorkspaceHolder, Connectable{
	
	public final ObjectProperty<ConnectionType> connectionTypeProperty(){
		if(connectionType == null)
			connectionType = new SimpleObjectProperty<ConnectionType>(this, "connection") {
				@Override
				public void set(ConnectionType newValue) {
					super.set(newValue == null ? ConnectionType.NONE : newValue);
				}

				@Override
				public void invalidated() {
					requestLayout();
				}
			};
		return connectionType;
	}
	private ObjectProperty<ConnectionType> connectionType;
	public final ConnectionType getConnectionType() { return connectionType == null ? ConnectionType.NONE : connectionTypeProperty().get(); }
	public final void setConnectionType(ConnectionType value) { connectionTypeProperty().set(value); }
    
	public final BooleanProperty movableProperty() {
		if (movable == null) 
			movable = new SimpleBooleanProperty(this, "movable");
		return movable;
	}
	private BooleanProperty movable;
	public boolean isMovable() {return movable == null ? true : movableProperty().get();}
	public final void setMovable(boolean value) {movableProperty().set(value);}
	
	public final BooleanProperty foldedProperty() {
		if (folded == null) 
			folded = new SimpleBooleanProperty(this, "folded") {
				@Override
				protected void invalidated() {
					pseudoClassStateChanged(PSEUDO_CLASS_FOLDED, get());
					requestLayout();
				}
			};
		return folded;
	}
	private BooleanProperty folded;
	public boolean isFolded() {return folded == null ? false : foldedProperty().get();}
	public final void setFolded(boolean value) {foldedProperty().set(value);}
	
	public final BooleanProperty defaultBlockProperty() {
		if (defaultBlock == null) 
			defaultBlock = new SimpleBooleanProperty(this, "defaultBlock") {
				@Override
				protected void invalidated() {
					pseudoClassStateChanged(PSEUDO_CLASS_DEFAULT, get());
				}
			};
		return defaultBlock;
	}
	private BooleanProperty defaultBlock;
	public boolean isDefaultBlock() {return defaultBlock == null ? false : defaultBlockProperty().get();}
	public final void setDefaultBlock(boolean value) {defaultBlockProperty().set(value);}
	
	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}
	
	public final ObjectProperty<Paint> fillProperty(){
		if(fill == null)
			fill = new SimpleObjectProperty<Paint>(this, "fill", Color.WHITE);
		return fill;
	}
	private ObjectProperty<Paint> fill;
	public final Paint getFill() {return fill == null ? Color.WHITE : fill.get();}
	public final void setFill(Paint value) {fillProperty().set(value);}
	
	public final ObjectProperty<Paint> strokeProperty(){
		if(stroke == null)
			stroke = new SimpleObjectProperty<Paint>(this, "stroke", Color.BLACK);
		return stroke;
	}
	private ObjectProperty<Paint> stroke;
	public final Paint getStroke() {return stroke == null ? Color.BLACK : stroke.get();}
	public final void setStroke(Paint value) {strokeProperty().set(value);}
	
	private ReadOnlyBooleanWrapper movingPropertyImpl() {
		if(moving == null)
			moving = new ReadOnlyBooleanWrapper(this, "moving") {
				@Override
				protected void invalidated() {
					pseudoClassStateChanged(PSEUDO_CLASS_MOVING, get());
				}
			};
		return moving;
	}
	private ReadOnlyBooleanWrapper moving;
	public final ReadOnlyBooleanProperty movingProperty() {return movingPropertyImpl().getReadOnlyProperty();}
	public final boolean isMoving() {return moving == null ? false : moving.get();}
	private void setMoving(boolean moving) {movingPropertyImpl().set(moving);}
	
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspacePropertyImpl(){
		if(workspace==null)
			workspace = new ReadOnlyObjectWrapper<FXBlockWorkspace>(this, "workspace");
		return workspace;
	}
	private ReadOnlyObjectWrapper<FXBlockWorkspace> workspace;
	public final FXBlockWorkspace getWorkspace() {return workspace == null ? null : workspace.get();}
	public final ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty() {return workspacePropertyImpl().getReadOnlyProperty();}
	
	private final ObservableList<FXBlockRow> fxRows = FXCollections.observableList(new LinkedList<>());
	
	public final ObservableList<FXBlockRow> getFXRows() {
		return fxRows;
	}
	
	private SVGPath dragSVGPath;
	protected final SVGPath getDragSVGPath() {
		return dragSVGPath;
	}
	protected final void setDragSVGPath(SVGPath svg) {
		this.dragSVGPath = svg;
	}
	
	private static final String DEFAULT_STYLE_CLASS = "block";
	
	public FXBlock() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		initBlockDragListener();
		
		setPickOnBounds(false); // 启用不规则图形判断
	}
	
	private double tempOldX, tempOldY;
	
	private void initBlockDragListener(){
		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			FXBlockWorkspace workspace = getWorkspace();
			if(workspace == null)
				return;
			
			if (!isMovable())
				return;
			
			Point2D nodePos = localToScene(0, 0);
			if (!contains(event.getSceneX() - nodePos.getX(), event.getSceneY() - nodePos.getY()))
				return;
			
			addToWorkspace();

			Point2D workspacePos = workspace.localToScene(0, 0);
			tempOldX = event.getSceneX() - nodePos.getX() + workspacePos.getX();
			tempOldY = event.getSceneY() - nodePos.getY() + workspacePos.getY();
			
			setMoving(true);
			workspace.setMovingBlock(this);
			
			event.consume();
		});
		addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			if (!isMoving())
				return;
			
			setLayoutX(event.getSceneX() - tempOldX);
			setLayoutY(event.getSceneY() - tempOldY);
			
			event.consume();
		});
		addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			setMoving(false);
			
			FXBlockWorkspace workspace = getWorkspace();
			if(workspace != null) {
				workspace.setMovingBlock(null);
				if(getConnectionType().isConnectable())
					workspace.connect(this, getConnectionBounds());
			}
			
			event.consume();
		});
	}
	
	private void initWorkspaceListener(){
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(newValue instanceof BlockWorkspaceHolder)
				workspacePropertyImpl().bind(((BlockWorkspaceHolder)newValue).workspaceProperty());
			else
				workspacePropertyImpl().unbind();
		});
	}
	
	public void addToWorkspace() {
		addToWorkspace(0, 0);
	}
	
	public void addToWorkspace(double offestX, double offestY) {
		Parent parent = getParent();
		if (parent instanceof FXBlockWorkspace) {
			toFront();
			setLayoutX(getLayoutX() + offestX);
			setLayoutY(getLayoutY() + offestY);
		} else {
			Point2D nodePos = localToScene(0, 0);
			Point2D workspacePos = getWorkspace().localToScene(0, 0);
			getWorkspace().getBlocks().add(this);
			setLayoutX(nodePos.getX() - workspacePos.getX() + offestX);
			setLayoutY(nodePos.getY() - workspacePos.getY() + offestY);
		}
	}
	
	public void removeBlock(){
		Parent parent = getParent();
		if(parent instanceof FXBlockRow)
			((FXBlockRow) parent).setBlock(null);
		else if (parent instanceof FXBlockWorkspace)
			((FXBlockWorkspace) parent).getBlocks().remove(this);
	}
	
	protected Bounds getConnectionBounds(){
		final double x = getLayoutX(), y = getLayoutY();
		switch (getConnectionType()) {
		case TOP:
			return new BoundingBox(x + TOP_OFFSET_X , y, TOP_WIDTH, TOP_HEIGHT);
		case LEFT:
			return new BoundingBox(x, y + LEFT_OFFSET_Y, LEFT_WIDTH , LEFT_HEIGHT);
		default:
			return null;
		}
	}
	
	protected boolean isConnectable(FXBlockRow row) {
		return true;
	}
	
	@Override
	public ConnectionResult connect(FXBlock block, Bounds bounds) {
		if(block == this)
			return ConnectionResult.FAILURE;
		
		if (!getBoundsInParent().intersects(bounds))
			return ConnectionResult.FAILURE;
		
		final Bounds subBounds = FXHelper.subtractBounds2D(bounds, this.getLayoutX(), this.getLayoutY());
		for(FXBlockRow row : getFXRows()) {
			ConnectionResult result = row.connect(block, subBounds);
			if(result != ConnectionResult.FAILURE)
				return result;
		}
		return ConnectionResult.FAILURE;
	}
	
	@Override
	public boolean contains(double localX, double localY) {
		return dragSVGPath.contains(localX, localY);
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockSkin(this);
	}
	
	public Block toModel() {
		Block block = new Block();
		block.setName(getName());
		for (FXBlockRow row : getFXRows()) {
			block.addRow(row.toModel(), row.getName());
		}
		return block;
	}
	
	private static final PseudoClass PSEUDO_CLASS_MOVING = PseudoClass.getPseudoClass("moving");
	private static final PseudoClass PSEUDO_CLASS_FOLDED = PseudoClass.getPseudoClass("folded");
	private static final PseudoClass PSEUDO_CLASS_DEFAULT = PseudoClass.getPseudoClass("default");
}
