package org.estatio.dom;

import org.apache.isis.applib.DomainObjectContainer;

public interface PowerType<T> {
    
    T create(DomainObjectContainer container); 


}
