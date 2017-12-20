package org.incode.module.classification.dom.contracttests.with;

import com.google.common.collect.ImmutableMap;

import org.incode.module.base.dom.with.ComparableByReferenceContractTestAbstract_compareTo;
import org.incode.module.base.dom.with.WithReferenceComparable;

/**
 * Automatically tests all domain objects implementing {@link WithReferenceComparable}.
 */
public class WithReferenceComparableContractForIncodeModuleTest_compareTo extends
        ComparableByReferenceContractTestAbstract_compareTo {

    public WithReferenceComparableContractForIncodeModuleTest_compareTo() {
        super("org.incode.module.classification", ImmutableMap.<Class<?>, Class<?>>of());
    }

}
