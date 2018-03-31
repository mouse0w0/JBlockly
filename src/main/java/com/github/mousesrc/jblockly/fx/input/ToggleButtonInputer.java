package com.github.mousesrc.jblockly.fx.input;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

public class ToggleButtonInputer extends ToggleButton implements Inputer<Boolean> {
	
	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}

	@Override
	public Boolean getValue() {
		return isSelected();
	}

	@Override
	public void setValue(Boolean value) {
		setSelected(value);
	}

	public ToggleButtonInputer() {
	}

	public ToggleButtonInputer(String text) {
		super(text);
	}

	public ToggleButtonInputer(String text, Node graphic) {
		super(text, graphic);
	}
}
