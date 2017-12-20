package org.incode.module.document.dom.contracttests.with;

import com.google.common.collect.ImmutableMap;

import org.incode.module.base.dom.with.ComparableByCodeContractTestAbstract_compareTo;
import org.incode.module.base.dom.with.WithCodeComparable;

/**
 * Automatically tests all domain objects implementing {@link WithCodeComparable}.
 */
public class WithCodeComparableContractForIncodeModuleTest_compareTo extends
        ComparableByCodeContractTestAbstract_compareTo {

    public WithCodeComparableContractForIncodeModuleTest_compareTo() {
        super("org.incode.module.document", ImmutableMap.<Class<?>,Class<?>>of());
    }

}
