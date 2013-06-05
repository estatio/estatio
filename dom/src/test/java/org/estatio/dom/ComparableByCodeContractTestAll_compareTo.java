package org.estatio.dom;

import com.google.common.collect.ImmutableMap;


/**
 * Automatically tests all domain objects implementing {@link ComparableByCode}.
 */
public class ComparableByCodeContractTestAll_compareTo extends ComparableByCodeContractTestAbstract_compareTo {

    public ComparableByCodeContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
