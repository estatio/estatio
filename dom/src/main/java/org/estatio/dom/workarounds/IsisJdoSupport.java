package org.estatio.dom.workarounds;

public interface IsisJdoSupport {

    /**
     * Inject services and container into a domain object.
     * 
     * <p>
     * Because we can't figure out why JDO/DN is not calling our callback
     * when an object is lazily loaded (LeaseItem ->* LeaseTerm).
     */
    <T> T injected(T domainObject);
    
    <T> T refresh(T domainObject);
}
