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

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotPersisted;


/**
 * For entities that can be locked/unlocked according to their status.
 * 
 * <p>
 * Note: originally the {@link #getStatus() status'} type was declared as a generic type (<tt>S extends Locable</tt>).
 * However, suspect that the JDO enhancer has a bug, because getting the exception:
 * <pre>
 * java.lang.VerifyError: (class: org/estatio/dom/lease/Lease, method: setStatus signature: 
 *      (Lorg/estatio/dom/Lockable;)V) Incompatible argument to function
 *     at java.lang.Class.getDeclaredConstructors0(Native Method)
 *     at java.lang.Class.privateGetDeclaredConstructors(Unknown Source)
 *     at java.lang.Class.getConstructor0(Unknown Source)
 *     at java.lang.Class.newInstance0(Unknown Source)
 *     at java.lang.Class.newInstance(Unknown Source)
 *     at org.estatio.dom.FixtureDatumFactoriesForAnyPojo.pojos(FixtureDatumFactoriesForAnyPojo.java:28)
 *     at org.estatio.dom.AbstractBeanPropertiesTest.pojos(AbstractBeanPropertiesTest.java:47)
 *     at org.estatio.dom.lease.LeaseItemTest_beanProperties.test(LeaseItemTest_beanProperties.java:35)
 * </pre>
 * <p>
 * For this reason, the property is now declared simply as {@link Lockable}; subclasses should downcast as need be. 
 */
public interface WithLockable<T,L extends Lockable> {

    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    @Hidden
    public L getLockable();
    public void setLockable(L lockable);

    @Hidden
    public boolean isLocked();
    
    @ActionSemantics(Of.IDEMPOTENT)
    public T lock();
    public boolean hideLock();

    @ActionSemantics(Of.IDEMPOTENT)
    public T unlock();
    public boolean hideUnlock();
    
}
