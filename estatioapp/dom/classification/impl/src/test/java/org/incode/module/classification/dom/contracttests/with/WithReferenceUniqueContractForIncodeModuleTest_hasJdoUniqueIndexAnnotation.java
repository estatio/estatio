package org.incode.module.classification.dom.contracttests.with;

import org.incode.module.base.dom.with.WithFieldUniqueContractTestAllAbstract;
import org.incode.module.base.dom.with.WithReferenceUnique;

public class WithReferenceUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation extends
        WithFieldUniqueContractTestAllAbstract<WithReferenceUnique> {

    public WithReferenceUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation() {
        super("org.incode.module.classification", "reference", WithReferenceUnique.class);
    }

}
