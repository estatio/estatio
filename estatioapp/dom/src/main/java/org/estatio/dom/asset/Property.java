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
import org.apache.isis.applib.annotation.Hidden;
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
@DomainObject(autoCompleteRepository = Properties.class)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Property
        extends FixedAsset<Property>
        implements Locatable, WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted {

    // //////////////////////////////////////

    private String applicationTenancyPath;

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Hidden
    public String getApplicationTenancyPath() {
        return applicationTenancyPath;
    }

    public void setApplicationTenancyPath(final String applicationTenancyPath) {
        this.applicationTenancyPath = applicationTenancyPath;
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancies.findTenancyByPath(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    private String fullName;

    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    // //////////////////////////////////////

    private PropertyType type;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    public PropertyType getType() {
        return type;
    }

    public void setType(final PropertyType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate openingDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(final LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate acquireDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public LocalDate getAcquireDate() {
        return acquireDate;
    }

    public void setAcquireDate(final LocalDate acquireDate) {
        this.acquireDate = acquireDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate disposalDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public LocalDate getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(final LocalDate disposalDate) {
        this.disposalDate = disposalDate;
    }

    // //////////////////////////////////////

    private BigDecimal area;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getArea() {
        return area;
    }

    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    // //////////////////////////////////////

    private String city;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.PROPER_NAME)
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    // //////////////////////////////////////

    private Country country;

    @javax.jdo.annotations.Column(name = "countryId", allowsNull = "true")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private Location location;

    @Override
    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

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
    private SortedSet<Unit> units = new TreeSet<Unit>();

    @CollectionLayout(render = RenderType.EAGERLY)
    @Deprecated
    public SortedSet<Unit> getUnits() {
        return units;
    }

    @Deprecated
    public void setUnits(final SortedSet<Unit> units) {
        this.units = units;
    }

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
        return unitsRepo.newUnit(this, reference, name, type);
    }

    // //////////////////////////////////////

    public Property dispose(
            final @ParameterLayout(named = "Disposal date") LocalDate disposalDate,
            final @ParameterLayout(named = "Are you sure?") boolean confirm) {
        if (confirm) {
            setDisposalDate(disposalDate);
        }
        return this;
    }

    public String disableDispose(LocalDate disposalDate, boolean confirm) {
        return getDisposalDate() == null ? null : "Property already disposed";
    }

    // //////////////////////////////////////

    @Inject
    Units unitsRepo;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject
    LocationLookupService locationLookupService;

}
