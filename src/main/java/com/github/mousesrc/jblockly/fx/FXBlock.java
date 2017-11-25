package com.github.mousesrc.jblockly.fx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.github.mousesrc.jblockly.api.Block;
import com.github.mousesrc.jblockly.api.BlockRow;
import com.github.mousesrc.jblockly.fx.skin.FXBlockSkin;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;

import static com.github.mousesrc.jblockly.fx.FXBlockGlobal.*;

public class FXBlock extends Control implements Block,BlockWorkspaceHolder,Connectable{
	
	private ObjectProperty<ConnectionType> connectionType;
	public final ObjectProperty<ConnectionType> connectionTypeProperty(){
		if(connectionType == null)
			connectionType = new SimpleObjectProperty<ConnectionType>(this, "connection") {
                @Override
                public void invalidated() {
                    requestLayout();
                }
			};
		return connectionType;
	}
	public final ConnectionType getConnectionType() {
		ConnectionType local = connectionType == null ? ConnectionType.NONE : connectionTypeProperty().get();
		return local == null ? ConnectionType.NONE : local;}
	public final void setConnectionType(ConnectionType value) {connectionTypeProperty().set(value);}
	
	private BooleanProperty movable;
	public final BooleanProperty movableProperty() {
		if (movable == null) 
			movable = new SimpleBooleanProperty(this, "movable");
		return movable;
	}
	public boolean isMovable() {return movable == null ? true : movableProperty().get();}
	public final void setMovable(boolean value) {movableProperty().set(value);}
	
	private StringProperty name;
	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}
	
	private ReadOnlyBooleanWrapper moving;
	private ReadOnlyBooleanWrapper movingPropertyImpl() {
		if(moving == null)
			moving = new ReadOnlyBooleanWrapper(this, "moving");
		return moving;
	}
	public final ReadOnlyBooleanProperty movingProperty() {return movingPropertyImpl().getReadOnlyProperty();}
	public final boolean isMoving() {return moving == null ? false : moving.get();}
	private void setMoving(boolean moving) {movingPropertyImpl().set(moving);}
	
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
	
	private final ObservableList<FXBlockRow> fxRows = FXCollections.observableArrayList();
	private final List<BlockRow> rows = new LinkedList<>();
	private final List<BlockRow> unmodifiableRows = Collections.unmodifiableList(rows);
	
	private static final String DEFAULT_STYLE_CLASS = "block";
	
	public FXBlock() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		
		initWorkspaceListener();
		initBlockDragListener();
		initRowsListener();
		
		setPickOnBounds(false); // 启用不规则图形判断,具体见contains方法
	}
	
	private double tempOldX, tempOldY;
	
	private void initBlockDragListener(){
		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			event.consume();
			
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
		});
		addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			event.consume();
			
			if (!isMoving())
				return;
			
			setLayoutX(event.getSceneX() - tempOldX);
			setLayoutY(event.getSceneY() - tempOldY);
		});
		addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			event.consume();
			
			setMoving(false);
			
			FXBlockWorkspace workspace = getWorkspace();
			if(workspace != null)
				getWorkspace().connect(this,getConnectionPoint());
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
			}
		});
	}
	
	private void initRowsListener(){
		getFXRows().addListener(new ListChangeListener<FXBlockRow>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FXBlockRow> c) {
				while(c.next()){
					rows.addAll(c.getAddedSubList());
					rows.removeAll(c.getRemoved());
				}
				requestLayout();
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
	
	public Point2D getConnectionPoint() {
		final double x = getLayoutX(), y = getLayoutY();
		switch (getConnectionType()) {
		case TOP:
		case TOP_AND_BOTTOM:
			return new Point2D(x + NEXT_OFFSET_X + NEXT_WIDTH / 2, y - 2.5);
		case LEFT:
			return new Point2D(x - 2.5, y + INSERT_OFFSET_Y + INSERT_HEIGHT / 2);
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
		return unmodifiableRows;
	}
	
	@Override
	public boolean connect(FXBlock block, Point2D point) {
		if(!contains(point))
			return false;
		return getFXRows().stream().anyMatch(row->row.connect(block, point.subtract(row.getLayoutX(), row.getLayoutY())));
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FXBlockSkin(this);
	}
}
