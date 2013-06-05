package org.estatio.dom;

import com.google.common.collect.ImmutableMap;


/**
 * Automatically tests all domain objects implementing {@link ComparableByDescription}.
 */
public class ComparableByDescriptionContractTestAll_compareTo extends ComparableByDescriptionContractTestAbstract_compareTo {

    public ComparableByDescriptionContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
