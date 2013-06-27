package org.estatio.dom.contracttests;

import org.estatio.dom.WithCodeUnique;


public class WithCodeUniqueContractTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueContractTestAllAbstract<WithCodeUnique> {

    public WithCodeUniqueContractTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithCodeUnique.class, "code");
    }

}
