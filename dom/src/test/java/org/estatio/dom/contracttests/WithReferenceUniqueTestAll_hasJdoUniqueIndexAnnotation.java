package org.estatio.dom.contracttests;

import org.estatio.dom.WithReferenceUnique;


public class WithReferenceUniqueTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueTestAllAbstract<WithReferenceUnique> {

    public WithReferenceUniqueTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithReferenceUnique.class, "reference");
    }

}
