package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.ComparableByDescription;
import org.estatio.dom.ComparableByDescriptionContractTestAbstract_compareTo;


/**
 * Automatically tests all domain objects implementing {@link ComparableByDescription}.
 */
public class ComparableByDescriptionContractTestAll_compareTo extends ComparableByDescriptionContractTestAbstract_compareTo {

    public ComparableByDescriptionContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
