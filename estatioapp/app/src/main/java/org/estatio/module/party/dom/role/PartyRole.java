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
package org.estatio.module.party.dom.role;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "PartyRole_party_roleType_UNQ", members = { "party", "roleType" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.role.PartyRole "
                        + "WHERE party == :party"
        ),
        @javax.jdo.annotations.Query(
                name = "findByRoleType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.role.PartyRole "
                        + "WHERE roleType == :roleType"
        ),
        @javax.jdo.annotations.Query(
                name = "findByPartyAndRoleType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.role.PartyRole "
                        + "WHERE party    == :party "
                        + "   && roleType == :roleType"
        )
})
@DomainObject()
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class PartyRole
        extends UdoDomainObject2<PartyRole> {

    public PartyRole() {
        super("party, roleType");
    }

    public PartyRole(final Party party, final PartyRoleType partyRoleType) {
        this();
        setParty(party);
        setRoleType(partyRoleType);
    }

    public String title() {
        return titleService.titleOf(getParty()) + " has role " + titleService.titleOf(getRoleType());
    }

    @Column(allowsNull = "false", name = "partyId")
    @Getter @Setter
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private Party party;

    @Column(allowsNull = "false", name = "typeId")
    @Getter @Setter
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private PartyRoleType roleType;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Party remove() {
        Party party = getParty();
        remove(this);
        return party;
    }

    @MemberOrder(name = "organisationsWithRoleType", sequence = "1")
    public PartyRole addRole(final Organisation organisation){
        partyRoleRepository.findOrCreate(organisation, this.getRoleType());
        return this;
    }

    @MemberOrder(name = "personsWithRoleType", sequence = "1")
    public PartyRole addRole(final Person person){
        partyRoleRepository.findOrCreate(person, this.getRoleType());
        return this;
    }



    @Override
    @Property
    @PropertyLayout(hidden = Where.ALL_TABLES)
    public ApplicationTenancy getApplicationTenancy() {
        return getParty().getApplicationTenancy();
    }


    @Inject
    TitleService titleService;


    @Inject
    PartyRoleRepository partyRoleRepository;

}
