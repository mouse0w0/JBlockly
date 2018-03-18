package com.github.mousesrc.jblockly.demo;

import com.github.mousesrc.jblockly.fx.FXBlock;
import com.github.mousesrc.jblockly.fx.FXBlockRow;
import com.github.mousesrc.jblockly.fx.FXBlockWorkspace;
import com.github.mousesrc.jblockly.fx.FXBlockRow.Type;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Demo extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXBlockWorkspace workspace = new FXBlockWorkspace();
		workspace.setPrefSize(800, 600);
		
		FXBlock block = new FXBlock();
		
		Label label = new Label("233333333333");
		FXBlockRow blockRow1 = new FXBlockRow();
		blockRow1.getComponents().addAll(label);
		
		FXBlockRow blockRow2 = new FXBlockRow();
		blockRow2.setType(Type.BRANCH);
		blockRow2.getComponents().addAll(new Label("233333333333"));
		
		FXBlockRow blockRow3 = new FXBlockRow();
		blockRow3.getComponents().addAll(new Label("233333333333"));
		
		block.getFXRows().addAll(blockRow1,blockRow2,blockRow3);
		
		workspace.getBlocks().addAll(block);
		
		Scene scene = new Scene(workspace);
		primaryStage.setScene(scene);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setTitle("JBlockly Demo");
		primaryStage.show();
	}

}
