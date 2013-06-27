package org.estatio.dom.contracttests;

import org.estatio.dom.WithTitleUnique;


public class WithTitleUniqueTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueTestAllAbstract<WithTitleUnique> {

    public WithTitleUniqueTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithTitleUnique.class, "title");
    }

}
