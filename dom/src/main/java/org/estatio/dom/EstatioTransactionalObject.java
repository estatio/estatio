package org.estatio.dom;

import javax.jdo.JDOHelper;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.annotation.ActionSemantics.Of;

/**
 * A domain object that is mutable and can be changed by multiple users over time,
 * and should therefore have optimistic locking controls in place.
 * 
 * <p>
 * Subclasses must be annotated with:
 * <pre>
 * @javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
 * public class MyDomainObject extends EstationTransactionalObject {
 *   ...
 * }
 * </pre>
 */
public abstract class EstatioTransactionalObject<T extends EstatioDomainObject<T>, S extends Lockable> extends EstatioDomainObject<T> implements WithStatus<T,S> {

    private final S statusToLock;
    private final S statusToUnlock;

    public EstatioTransactionalObject(String keyProperties, S statusToLock, S statusToUnlock) {
        super(keyProperties);
        this.statusToLock = statusToLock;
        this.statusToUnlock = statusToUnlock;
    }

    @Hidden
    public String getId() {
        final String id = JDOHelper.getObjectId(this).toString().split("\\[OID\\]")[0];
        return id;
    }

    
    
    // //////////////////////////////////////
    
    public void created() {
        setStatus(statusToLock);
    }
    
    // //////////////////////////////////////

    @Hidden
    public Long getVersionSequence() {
        final Long version = (Long) JDOHelper.getVersion(this);
        return version;
    }

    // //////////////////////////////////////
    
    @MemberOrder(sequence = "4.5")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Override
    public boolean isLocked() {
        return getStatus().isLocked();
    }

    @SuppressWarnings("unchecked")
    @Bulk
    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="locked", sequence = "1")
    @Override
    public T lock() {
        // guard against invalid updates when called as bulk action
        if (hideLock()) {
            return (T) this;
        } 
        setStatus(statusToLock);
        return (T) this;
    }

    @Override
    public boolean hideLock() {
        return statusToLock == null || getStatus().isLocked();
    }
    
    @SuppressWarnings("unchecked")
    @Bulk
    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="locked", sequence = "2")
    @Override
    public T unlock() {
        // guard against invalid updates when called as bulk action
        if (hideUnlock()) {
            return (T) this;
        } 
        setStatus(statusToUnlock);
        return (T) this;
    }
    
    @Override
    public boolean hideUnlock() {
        return statusToUnlock == null || getStatus().isUnlocked();
    }

    

}
