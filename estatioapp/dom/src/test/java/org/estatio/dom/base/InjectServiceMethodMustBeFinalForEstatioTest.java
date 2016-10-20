package org.estatio.dom.base;

import org.apache.isis.core.unittestsupport.inject.InjectServiceMethodMustBeFinalContractTestAbstract;

public class InjectServiceMethodMustBeFinalForEstatioTest extends InjectServiceMethodMustBeFinalContractTestAbstract {

    public InjectServiceMethodMustBeFinalForEstatioTest() {
        super("org.estatio");
        withLoggingTo(System.out);
    }

}
