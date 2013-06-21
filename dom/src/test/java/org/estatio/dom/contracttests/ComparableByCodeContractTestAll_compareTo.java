package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.ComparableByCode;
import org.estatio.dom.ComparableByCodeContractTestAbstract_compareTo;


/**
 * Automatically tests all domain objects implementing {@link ComparableByCode}.
 */
public class ComparableByCodeContractTestAll_compareTo extends ComparableByCodeContractTestAbstract_compareTo {

    public ComparableByCodeContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
