package org.estatio.dom;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

public abstract class EstatioDomainObject extends AbstractDomainObject {

    protected IsisJdoSupport isisJdoSupport;
    public void setIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }
    
}
