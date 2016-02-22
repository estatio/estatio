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
package org.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.wicket.gmap3.cpt.applib.Locatable;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.Property "
                        + "WHERE reference.matches(:referenceOrName)"
                        + "|| name.matches(:referenceOrName)"),
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.Property "
                        + "WHERE reference == :reference")
})
@DomainObject(autoCompleteRepository = PropertyRepository.class)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Property
        extends FixedAsset<Property>
        implements Locatable, WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted {



    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
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

    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String fullName;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Getter @Setter
    private PropertyType type;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate openingDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate acquireDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate disposalDate;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal area;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PROPER_NAME)
    @Getter @Setter
    private String city;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "countryId", allowsNull = "true")
    @Getter @Setter
    private Country country;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Location location;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Lookup")
    public FixedAsset lookupLocation(
            final @ParameterLayout(named = "Address", describedAs = "Example: Herengracht 469, Amsterdam, NL") String address) {
        if (locationLookupService != null) {
            // TODO: service does not seem to be loaded in tests
            setLocation(locationLookupService.lookup(address));
        }
        return this;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "property")
    @CollectionLayout(render = RenderType.EAGERLY)
    @Deprecated
    @Getter @Setter
    private SortedSet<Unit> units = new TreeSet<>();

    // //////////////////////////////////////

    /**
     * For use by Api and by fixtures.
     */
    @Programmatic
    public FixedAssetRole addRoleIfDoesNotExist(
            final @ParameterLayout(named = "party") Party party,
            final @ParameterLayout(named = "type") FixedAssetRoleType type,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "End Date") LocalDate endDate) {

        FixedAssetRole role = fixedAssetRoleRepository.findRole(this, party, type, startDate, endDate);
        if (role == null) {
            role = this.createRole(type, party, startDate, endDate);
        }
        return role;
    }

    @Programmatic
    public Unit newUnit(final String reference, final String name, final UnitType type) {
        return unitMenuRepo.newUnit(this, reference, name, type);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Property dispose(
            final @ParameterLayout(named = "Disposal date") LocalDate disposalDate
    ) {
        setDisposalDate(disposalDate);
        return this;
    }

    public String disableDispose(LocalDate disposalDate) {
        return getDisposalDate() == null ? null : "Property already disposed";
    }

    // //////////////////////////////////////

    @Inject
    UnitMenu unitMenuRepo;

    @Inject
    LocationLookupService locationLookupService;

}
