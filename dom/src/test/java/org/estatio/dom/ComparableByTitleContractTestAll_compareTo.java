package org.estatio.dom;

import com.google.common.collect.ImmutableMap;


/**
 * Automatically tests all domain objects implementing {@link ComparableByTitle}.
 */
public class ComparableByTitleContractTestAll_compareTo extends ComparableByTitleContractTestAbstract_compareTo {

    public ComparableByTitleContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
