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
    
    @Hidden
    @Override
    public boolean isLocked() {
        return getStatus().isLocked();
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    @SuppressWarnings("unchecked")
    public T lock() {
        setStatus(statusToLock);
        return (T) this;
    }

    @Override
    public boolean hideLock() {
        return statusToLock == null || getStatus().isLocked();
    }
    
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    @SuppressWarnings("unchecked")
    public T unlock() {
        setStatus(statusToUnlock);
        return (T) this;
    }
    
    @Override
    public boolean hideUnlock() {
        return statusToUnlock == null || getStatus().isUnlocked();
    }

    

}
