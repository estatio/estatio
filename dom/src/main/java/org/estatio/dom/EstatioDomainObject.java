package org.estatio.dom;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.util.ObjectContracts;

public abstract class EstatioDomainObject<T extends EstatioDomainObject<T>> extends AbstractDomainObject implements Comparable<T> {

    private static ObjectContracts estatioObjectContracts = 
            new ObjectContracts()
                .with(WithCodeGetter.ToString.evaluator())
                .with(WithDescriptionGetter.ToString.evaluator())
                .with(WithNameGetter.ToString.evaluator())
                .with(WithReferenceGetter.ToString.evaluator())
                .with(WithTitleGetter.ToString.evaluator());

    private final String keyProperties;

    public EstatioDomainObject(String keyProperties) {
        this.keyProperties = keyProperties;
    }

    protected String keyProperties() {
        return keyProperties;
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return estatioObjectContracts.toStringOf(this, keyProperties());
    }

    @Override
    public int compareTo(T other) {
        return ObjectContracts.compare(this, other, keyProperties);
    }

}
