package com.github.mousesrc.jblockly.fx;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;

public class FXBlockWorkspaceSkin extends SkinBase<FXBlockWorkspace>{

	protected FXBlockWorkspaceSkin(FXBlockWorkspace control) {
		super(control);
		
		initBlocksListener();
	}
	
	private boolean removingBlock = false;
	private void initBlocksListener(){
		getChildren().addAll(getSkinnable().getBlocks());
		getSkinnable().getBlocks().addListener(new ListChangeListener<FXBlock>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FXBlock> c) {
				while(c.next()){
					getChildren().addAll(c.getAddedSubList());
					if(!removingBlock){
						removingBlock = true;
						getChildren().removeAll(c.getRemoved());
						removingBlock = false;
					}
				}
			}
		});
		getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while(c.next()){
					if(!removingBlock){
						removingBlock = true;
						getSkinnable().getBlocks().removeAll(c.getRemoved());
						removingBlock = false;
					}
				}
			}
			
		});
	}

}
