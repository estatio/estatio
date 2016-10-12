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
package org.estatio.dom.lease.assignment;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.types.EnumType;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.lease.Lease;

import lombok.Getter;
import lombok.Setter;

//TODO: is this in scope?
@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
public class LeaseAssignment
        extends UdoDomainObject2<LeaseAssignment>
        implements WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted {

    
    public LeaseAssignment() {
        // TODO: I made this up...
        super("nextLease,assignmentDate");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(name="previousLeaseId")
    @Getter @Setter
    private Lease previousLease;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(name="nextLeaseId")
    @Getter @Setter
    private Lease nextLease;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="false")
    @Getter @Setter
    private LocalDate assignmentDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="false", length= EnumType.MAX_LEN)
    @Getter @Setter
    private LeaseAssignmentType type;

}
