package org.estatio.dom.contracttests;

import org.estatio.dom.WithTitleUnique;


public class WithTitleUniqueContractTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueContractTestAllAbstract<WithTitleUnique> {

    public WithTitleUniqueContractTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithTitleUnique.class, "title");
    }

}
