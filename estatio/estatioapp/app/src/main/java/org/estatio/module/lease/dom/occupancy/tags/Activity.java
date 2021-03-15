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
package org.estatio.module.lease.dom.occupancy.tags;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.*;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.types.DescriptionType;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.incode.module.base.dom.with.WithNameGetter;
import org.estatio.module.base.dom.apptenancy.ApplicationTenancyConstants;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyGlobal;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Activity_sector_name_UNQ", members = { "sector", "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findBySectorAndName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.tags.Activity "
                        + "WHERE sector == :sector "
                        + "   && name == :name")
})
@DomainObject(
        bounded = true,
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.lease.tags.Activity"
)
public class Activity
        extends UdoDomainObject2<Activity>
        implements WithNameGetter, WithApplicationTenancyGlobal {

    public Activity() {
        super("sector,name");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .toString();
    }

    @Property(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(
                ApplicationTenancyConstants.GLOBAL_PATH);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "sectorId", allowsNull = "false")
    @Getter @Setter
    private Sector sector;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length= NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false", length= DescriptionType.Meta.MAX_LEN)
    @Getter @Setter
    private String description;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private Integer sortOrder;

    public Activity change(
            final String description,
            final Integer sortOrder) {
        setDescription(description);
        setSortOrder(sortOrder);
        return this;
    }

    public String default0Change() {
        return getDescription();
    }

    public Integer default1Change() {
        return getSortOrder();
    }

    public boolean hideChange() {
        Person meAsPerson = personRepository.me();
        if (meAsPerson != null && meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.SECTOR_MAINTAINER.findUsing(partyRoleTypeRepository))) {
            return false;
        }
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
    }

    public void remove() {
        repositoryService.remove(this);
    }

    public String validateRemove() {
        return occupancyRepository.findByActivity(this).isEmpty() ? null : "Activity is already in use, cannot be removed";
    }

    public boolean hideRemove() {
        Person meAsPerson = personRepository.me();
        if (meAsPerson != null && meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.SECTOR_MAINTAINER.findUsing(partyRoleTypeRepository))) {
            return false;
        }
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(userService.getUser());
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    PersonRepository personRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    UserService userService;

    @Inject
    OccupancyRepository occupancyRepository;

}
