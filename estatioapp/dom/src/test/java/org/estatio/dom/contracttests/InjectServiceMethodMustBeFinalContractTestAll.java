package org.estatio.dom.contracttests;

import org.apache.isis.core.unittestsupport.inject.InjectServiceMethodMustBeFinalContractTestAbstract;

public class InjectServiceMethodMustBeFinalContractTestAll extends InjectServiceMethodMustBeFinalContractTestAbstract {

    public InjectServiceMethodMustBeFinalContractTestAll() {
        super("org.estatio.dom");
        withLoggingTo(System.out);
    }

}
