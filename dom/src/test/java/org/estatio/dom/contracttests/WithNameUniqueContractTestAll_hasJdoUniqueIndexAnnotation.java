package org.estatio.dom.contracttests;

import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceGetter;


public class WithNameUniqueContractTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueContractTestAllAbstract<WithNameUnique> {

    public WithNameUniqueContractTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithNameUnique.class, "name");
    }

}
