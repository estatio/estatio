package org.estatio.dom.contracttests;

import org.estatio.dom.WithReferenceUnique;


public class WithReferenceUniqueContractTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueContractTestAllAbstract<WithReferenceUnique> {

    public WithReferenceUniqueContractTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithReferenceUnique.class, "reference");
    }

}
