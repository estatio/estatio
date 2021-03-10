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
package org.estatio.module.base.dom;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

/**
 * A domain object that is mutable and can be changed by multiple users over
 * time, and should therefore have optimistic locking controls in place.
 * 
 * <p>
 * Subclasses must be annotated with:
 * 
 * <pre>
 * @javax.jdo.annotations.PersistenceCapable
 * @javax.jdo.annotations.DatastoreIdentity(
 *     strategy = IdGeneratorStrategy.NATIVE,
 *     column = "id")
 * @javax.jdo.annotations.Version(
 *     strategy=VersionStrategy.VERSION_NUMBER, 
 *     column="version")
 * public class MyDomainObject extends EstationMutableObject {
 *   ...
 * }
 * </pre>
 * 
 * <p>
 * Note however that if a subclass that has a supertype which is annotated with
 * {@link javax.jdo.annotations.Version} (eg <tt>CommunicationChannel</tt>) then
 * the subtype must not also have a <tt>Version</tt> annotation (otherwise JDO
 * will end up putting a <tt>version</tt> column in both tables, and they are
 * not kept in sync).
 */
// @PersistenceCapable  (all subclasses are annotated with this already)
// @Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class UdoDomainObject2<T extends UdoDomainObject2<T>>
        extends UdoDomainObject<T> {

    public UdoDomainObject2(
            final String keyProperties) {
        super(keyProperties);
    }


    @javax.inject.Inject
    protected ApplicationTenancyRepository securityApplicationTenancyRepository;

}
