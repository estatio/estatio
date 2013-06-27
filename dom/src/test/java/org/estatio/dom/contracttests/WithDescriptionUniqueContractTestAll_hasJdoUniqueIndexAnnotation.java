package org.estatio.dom.contracttests;

import org.estatio.dom.WithDescriptionUnique;


public class WithDescriptionUniqueContractTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueContractTestAllAbstract<WithDescriptionUnique> {

    public WithDescriptionUniqueContractTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithDescriptionUnique.class, "description");
    }

}
