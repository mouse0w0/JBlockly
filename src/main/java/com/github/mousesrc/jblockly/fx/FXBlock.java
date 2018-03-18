package com.github.mousesrc.jblockly.fx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.util.FXHelper;

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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;

import static com.github.mousesrc.jblockly.fx.FXBlockConstant.*;

public class FXBlock extends Control implements Block, BlockWorkspaceHolder, Connectable{
	
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
	
	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}
	
	private ReadOnlyBooleanWrapper movingPropertyImpl() {
		if(moving == null)
			moving = new ReadOnlyBooleanWrapper(this, "moving");
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
	private void setWorkspace(FXBlockWorkspace workspace) {workspacePropertyImpl().set(workspace);}
	private final ChangeListener<FXBlockWorkspace> workspaceListener = (observable, oldValue, newValue)->workspacePropertyImpl().set(newValue);
	
	private final ObservableList<FXBlockRow> fxRows = FXCollections.observableList(new LinkedList<>());
	
	private SVGPath dragSVGPath;
	protected SVGPath getDragSVGPath() {
		return dragSVGPath;
	}
	protected void setDragSVGPath(SVGPath svg) {
		this.dragSVGPath = svg;
	}
	
	private static final String DEFAULT_STYLE_CLASS = "block";
	
	public FXBlock() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		initBlockDragListener();
		
		setPickOnBounds(false); // 启用不规则图形判断
		setSnapToPixel(true);
	}
	
	private double tempOldX, tempOldY;
	
	private void initBlockDragListener(){
		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			FXBlockWorkspace workspace = getWorkspace();
			if(workspace == null)
				return;
			
			if (!isMovable())
				return;
			
			addToWorkspace();

			Point2D pos = FXHelper.getRelativePos(workspace, this);
			tempOldX = event.getSceneX() - pos.getX();
			tempOldY = event.getSceneY() - pos.getY();
			
			setMoving(true);
			
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
			if(workspace != null)
				workspace.connect(this, getConnectionBounds(workspace));
			
			event.consume();
		});
	}
	
	private void initWorkspaceListener(){
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(oldValue instanceof BlockWorkspaceHolder)
				((BlockWorkspaceHolder)oldValue).workspaceProperty().removeListener(workspaceListener);
			if(newValue instanceof BlockWorkspaceHolder){
				BlockWorkspaceHolder holder = (BlockWorkspaceHolder)newValue;
				setWorkspace(holder.workspaceProperty().get());
				holder.workspaceProperty().addListener(workspaceListener);
			}else{
				setWorkspace(null);
			}
		});
	}
	
	public void addToWorkspace() {
		if (getParent() instanceof FXBlockWorkspace) {
			toFront();
		} else {
			Point2D pos = FXHelper.getRelativePos(getWorkspace(), this);
			getWorkspace().getBlocks().add(this);
			setLayoutX(pos.getX());
			setLayoutY(pos.getY());
		}
	}
	
	public void removeBlock(){
		Parent parent = getParent();
		if(parent instanceof FXBlockRow)
			((FXBlockRow) parent).setBlock(null);
		else if (parent instanceof FXBlockWorkspace)
			((FXBlockWorkspace) parent).getBlocks().remove(this);
	}
	
	public Bounds getConnectionBounds(FXBlockWorkspace workspace){
		final Point2D pos = FXHelper.getRelativePos(workspace, this);
		final double x = pos.getX(), y = pos.getY();
		switch (getConnectionType()) {
		case TOP:
			return new BoundingBox(x + TOP_OFFSET_X , y, TOP_WIDTH, TOP_HEIGHT);
		case LEFT:
			return new BoundingBox(x, y + LEFT_OFFSET_Y, LEFT_WIDTH , LEFT_HEIGHT);
		default:
			return null;
		}
	}
	
	public ObservableList<FXBlockRow> getFXRows() {
		return fxRows;
	}
	
	@Override
	public String getBlockName() {
		return getName();
	}

	@Override
	public List<BlockRow> getRows() {
		return new ArrayList<>(fxRows);
	}
	
	@Override
	public boolean connect(FXBlock block, Bounds bounds) {
		if(block == this)
			return false;
		
		if (!getLayoutBounds().intersects(bounds))
			return false;
		
		for(FXBlockRow row : getFXRows())
			if(row.connect(block, FXHelper.subtractBounds2D(bounds, row.getLayoutX(), row.getLayoutY())))
					return true;
		return false;
	}
	
	@Override
	public boolean contains(double localX, double localY) {
		return dragSVGPath.contains(localX, localY);
	}
	
	@Override
	public boolean contains(Point2D localPoint) {
		return dragSVGPath.contains(localPoint);
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockSkin(this);
	}
}
