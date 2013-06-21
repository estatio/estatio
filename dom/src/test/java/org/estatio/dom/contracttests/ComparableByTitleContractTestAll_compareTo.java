package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.ComparableByTitle;
import org.estatio.dom.ComparableByTitleContractTestAbstract_compareTo;


/**
 * Automatically tests all domain objects implementing {@link ComparableByTitle}.
 */
public class ComparableByTitleContractTestAll_compareTo extends ComparableByTitleContractTestAbstract_compareTo {

    public ComparableByTitleContractTestAll_compareTo() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of());
    }

}
