package org.incode.module.classification.dom.contracttests.with;

import org.incode.module.base.dom.with.WithFieldUniqueContractTestAllAbstract;
import org.incode.module.base.dom.with.WithTitleUnique;

public class WithTitleUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation extends
        WithFieldUniqueContractTestAllAbstract<WithTitleUnique> {

    public WithTitleUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation() {
        super("org.incode.module.classification", "title", WithTitleUnique.class);
    }

}
