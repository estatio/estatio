package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.WithDescriptionComparable;
import org.estatio.dom.ComparableByDescriptionContractTestAbstract_compareTo;


/**
 * Automatically tests all domain objects implementing {@link WithDescriptionComparable}.
 */
public class WithDescriptionComparableContractTestAll_compareTo extends ComparableByDescriptionContractTestAbstract_compareTo {

    public WithDescriptionComparableContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
