package org.estatio.dom.contracttests;

import org.estatio.dom.WithCodeUnique;


public class WithCodeUniqueTestAll_hasJdoUniqueIndexAnnotation extends WithFieldUniqueTestAllAbstract<WithCodeUnique> {

    public WithCodeUniqueTestAll_hasJdoUniqueIndexAnnotation() {
        super(WithCodeUnique.class, "code");
    }

}
