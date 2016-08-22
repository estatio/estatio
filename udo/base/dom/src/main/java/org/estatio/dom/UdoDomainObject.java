/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import javax.inject.Inject;
import javax.jdo.JDOHelper;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;


/**
 * A domain object that is mutable and can be changed by multiple users over time,
 * and should therefore have optimistic locking controls in place.
 * <p/>
 * <p/>
 * Subclasses must be annotated with:
 * <pre>
 * @javax.jdo.annotations.DatastoreIdentity(
 *     strategy = IdGeneratorStrategy.NATIVE,
 *     column = "id")
 * @javax.jdo.annotations.Version(
 *     strategy=VersionStrategy.VERSION_NUMBER,
 *     column="version")
 * public class MyDomainObject extends EstatioDomainObject {
 *   ...
 * }
 * </pre>
 * <p/>
 * <p/>
 * Note however that if a subclass that has a supertype which is annotated
 * with {@link javax.jdo.annotations.Version} (eg <tt>CommunicationChannel</tt>)
 * then the subtype must not also have a <tt>Version</tt> annotation (otherwise JDO
 * will end up putting a <tt>version</tt> column in both tables, and they are not
 * kept in sync).
 */
public abstract class UdoDomainObject<T extends UdoDomainObject<T>>
        extends AbstractDomainObject
        implements Comparable<T>, WithApplicationTenancy {

    protected static ObjectContracts UDO_OBJECT_CONTRACTS =
            new ObjectContracts()
                    .with(WithReferenceGetter.ToString.evaluator())
                    .with(WithCodeGetter.ToString.evaluator())
                    .with(WithNameGetter.ToString.evaluator())
                    .with(WithTitleGetter.ToString.evaluator())
                    .with(WithDescriptionGetter.ToString.evaluator());

    private final String keyProperties;

    public UdoDomainObject(final String keyProperties) {
        this.keyProperties = keyProperties;
    }

    protected String keyProperties() {
        return keyProperties;
    }

    // //////////////////////////////////////

    @Programmatic
    public String getId() {
        Object objectId = JDOHelper.getObjectId(this);
        if (objectId == null) {
            return "";
        }
        String objectIdStr = objectId.toString();
        final String id = objectIdStr.split("\\[OID\\]")[0];
        return id;
    }

    // //////////////////////////////////////

    @Inject public ClockService clockService;

    protected ClockService getClockService() {
        return clockService;
    }

    /**
     * a default value is used to prevent null pointers for objects
     * being initialized where the service has not yet been injected into.
     */
    @Inject
    private EventBusService eventBusService = EventBusService.NOOP;

    protected EventBusService getEventBusService() {
        return eventBusService;
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return UDO_OBJECT_CONTRACTS.toStringOf(this, keyProperties());
    }

    @Override
    public int compareTo(final T other) {
        return ObjectContracts.compare(this, other, keyProperties);
    }

}
