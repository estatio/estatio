package org.estatio.dom;


public abstract class EstatioRefDataObject<T extends EstatioDomainObject<T>> extends EstatioDomainObject<T> {

    public EstatioRefDataObject(String keyProperties) {
        super(keyProperties);
    }

}
