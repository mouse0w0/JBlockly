package com.github.mousesrc.jblockly.api;

import java.util.Optional;

public interface BlockInput<T> {

	Optional<T> getInputValue();
}
