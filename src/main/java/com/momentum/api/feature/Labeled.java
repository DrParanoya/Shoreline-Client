package com.momentum.api.feature;

import com.momentum.api.registry.Registry;

/**
 * Data with label in {@link Registry} Registry
 *
 * @author linus
 * @since 02/09/2023
 */
public interface Labeled {

    /**
     * Gets the label
     *
     * @return The label
     */
    String getLabel();
}
