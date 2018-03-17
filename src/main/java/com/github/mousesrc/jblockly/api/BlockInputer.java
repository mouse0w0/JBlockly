package com.github.mousesrc.jblockly.api;

import java.util.Optional;

public interface BlockInputer<T> {

	Optional<T> getInputValue();
}
