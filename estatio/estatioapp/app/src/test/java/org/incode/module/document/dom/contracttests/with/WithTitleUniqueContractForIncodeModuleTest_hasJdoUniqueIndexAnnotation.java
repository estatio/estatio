package org.incode.module.document.dom.contracttests.with;

import org.incode.module.base.dom.with.WithFieldUniqueContractTestAllAbstract;
import org.incode.module.base.dom.with.WithTitleUnique;

public class WithTitleUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation extends
        WithFieldUniqueContractTestAllAbstract<WithTitleUnique> {

    public WithTitleUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation() {
        super("org.incode.module.document", "title", WithTitleUnique.class);
    }

}
