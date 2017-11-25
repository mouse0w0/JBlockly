package com.github.mousesrc.jblockly.fx;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface BlockWorkspaceHolder {
	ReadOnlyObjectProperty<FXBlockWorkspace> workspaceProperty();
}
