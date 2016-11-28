package org.estatio.dom.base;

import org.apache.isis.core.unittestsupport.inject.InjectServiceMethodMustBeFinalContractTestAbstract;

public class InjectServiceMethodMustBeFinalForEstatio_UnitTest extends InjectServiceMethodMustBeFinalContractTestAbstract {

    public InjectServiceMethodMustBeFinalForEstatio_UnitTest() {
        super("org.estatio");
        withLoggingTo(System.out);
    }

}
