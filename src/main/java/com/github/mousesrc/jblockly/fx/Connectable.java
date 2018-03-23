package com.github.mousesrc.jblockly.fx;

import javafx.geometry.Bounds;

public interface Connectable {

	ConnectionResult connect(FXBlock block, Bounds bounds);
}
