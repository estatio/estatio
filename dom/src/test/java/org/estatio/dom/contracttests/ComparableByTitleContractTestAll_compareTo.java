package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.WithTitleComparable;
import org.estatio.dom.ComparableByTitleContractTestAbstract_compareTo;


/**
 * Automatically tests all domain objects implementing {@link WithTitleComparable}.
 */
public class ComparableByTitleContractTestAll_compareTo extends ComparableByTitleContractTestAbstract_compareTo {

    public ComparableByTitleContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
