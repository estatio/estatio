package org.estatio.dom.contracttests;

import org.estatio.dom.WithDescriptionUnique;


public class WithDescriptionUniqueTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueTestAllAbstract<WithDescriptionUnique> {

    public WithDescriptionUniqueTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithDescriptionUnique.class, "description");
    }

}
