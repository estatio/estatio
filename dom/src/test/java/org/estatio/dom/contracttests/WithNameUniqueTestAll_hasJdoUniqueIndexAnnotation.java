package org.estatio.dom.contracttests;

import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceGetter;


public class WithNameUniqueTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueTestAllAbstract<WithNameUnique> {

    public WithNameUniqueTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithNameUnique.class, "name");
    }

}
