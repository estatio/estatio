package org.estatio.dom;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;


public interface WithStatus<T,S extends Lockable> {

    @Disabled
    public S getStatus();
    public void setStatus(S newStatus);

    @Disabled
    public boolean isLocked();
    
    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="locked", sequence="1")
    public T lock();
    
    public boolean hideLock();

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="locked", sequence="2")
    public T unlock();
    
    public boolean hideUnlock();
    
}
