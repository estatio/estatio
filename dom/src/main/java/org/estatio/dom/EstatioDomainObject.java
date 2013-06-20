package org.estatio.dom;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.util.ObjectContracts;

public abstract class EstatioDomainObject<T extends EstatioDomainObject<T>> extends AbstractDomainObject implements Comparable<T> {

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
        return ObjectContracts.toString(this, keyProperties());
    }

    @Override
    public int compareTo(T other) {
        return ObjectContracts.compare(this, other, keyProperties);
    }

}
