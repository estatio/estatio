/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import javax.jdo.JDOHelper;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.CssClass;
import org.apache.isis.applib.annotation.Hidden;

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

    /**
     * The status representing an unlocked - freely editable - object.
     */
    private final S statusWhenUnlocked;
    /**
     * The (single) status representing a locked (non-editable) object.
     * 
     * <p>
     * This will be <tt>null</tt> if the subclass does not support explicit locking/unlocking.
     */
    private final S statusWhenLockedIfAny;

    public EstatioTransactionalObject(String keyProperties, S statusWhenUnlocked, S statusWhenLockedIfAny) {
        super(keyProperties);
        this.statusWhenUnlocked = statusWhenUnlocked;
        this.statusWhenLockedIfAny = statusWhenLockedIfAny;
    }

    @Hidden
    public String getId() {
        final String id = JDOHelper.getObjectId(this).toString().split("\\[OID\\]")[0];
        return id;
    }

    

    // //////////////////////////////////////
    
    public void created() {
        setStatus(statusWhenLockedIfAny);
    }
    
    // //////////////////////////////////////

    @Hidden
    public Long getVersionSequence() {
        final Long version = (Long) JDOHelper.getVersion(this);
        return version;
    }

    // //////////////////////////////////////
    

    @Hidden
    @Override
    public boolean isLocked() {
        return getStatus()!=null? !getStatus().isUnlocked(): true;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @CssClass("lock")
    @Override
    @SuppressWarnings("unchecked")
    public T lock() {
        setStatus(statusWhenLockedIfAny);
        return (T) this;
    }

    @Override
    public boolean hideLock() {
        return cannotExplicitlyLockAndUnlock();
    }

    @CssClass("unlock")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    @SuppressWarnings("unchecked")
    public T unlock() {
        setStatus(statusWhenUnlocked);
        return (T) this;
    }
    
    @Override
    public boolean hideUnlock() {
        return cannotExplicitlyLockAndUnlock();
    }

    private boolean cannotExplicitlyLockAndUnlock() {
        return statusWhenLockedIfAny == null;
    }
    
    // //////////////////////////////////////

    /**
     * Disable (for all properties)
     */
    public String disabled(Identifier.Type type) {
        if(type == Identifier.Type.PROPERTY_OR_COLLECTION) {
            return isLocked()? "Cannot modify when locked": null;
        }
        return null;
    }


}
