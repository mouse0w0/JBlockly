package com.github.mousesrc.jblockly.fx.input;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class CheckBoxInputer extends CheckBox implements Inputer<Boolean>{

	public final StringProperty nameProperty(){
		if(name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}
	private StringProperty name;
	public final String getName() {return name == null ? null : nameProperty().get();}
	public final void setName(String name) {nameProperty().set(name);}
	
    public CheckBoxInputer() {
    }

    public CheckBoxInputer(String text) {
    	super(text);
    }
    
	@Override
	public Boolean getValue() {
		return isSelected();
	}

	@Override
	public void setValue(Boolean value) {
		setSelected(value);
	}
}
