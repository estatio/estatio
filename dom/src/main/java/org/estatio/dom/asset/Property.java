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
package org.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.danhaywood.isis.wicket.gmap3.applib.Locatable;
import com.danhaywood.isis.wicket.gmap3.applib.Location;
import com.danhaywood.isis.wicket.gmap3.service.LocationLookupService;

import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistingCallbackFacet;

@javax.jdo.annotations.PersistenceCapable
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
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
                        + "WHERE reference.matches(:reference)")
})
@AutoComplete(repository = Properties.class)
@Bookmarkable
public class Property extends FixedAsset implements Locatable {

    private PropertyType propertyType;

    @javax.jdo.annotations.Column(allowsNull = "false")
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final PropertyType type) {
        this.propertyType = type;
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

    @javax.jdo.annotations.Column(allowsNull = "true")
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
    @Disabled
    @Optional
    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Named("Lookup")
    public FixedAsset lookupLocation(final @Named("Address") String address) {
        setLocation(locationLookupService.lookup(address));
        return this;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "property")
    private SortedSet<Unit> units = new TreeSet<Unit>();

    @Render(Type.EAGERLY)
    public SortedSet<Unit> getUnits() {
        return units;
    }

    public void setUnits(final SortedSet<Unit> units) {
        this.units = units;
    }

    // //////////////////////////////////////

    @PublishedAction
    public Unit newUnit(
            @Named("Reference") @RegEx(validation = "[-/_A-Z0-9]+", caseSensitive=true) final String reference,
            @Named("Name") final String name,
            final UnitType unitType) {
        Unit unit = unitsRepo.newUnit(reference, name, unitType);
        unit.setProperty(this);
        return unit;
    }

    public String default0NewUnit() {
        return this.getReference() + "-000";
    }

    public String default1NewUnit() {
        return "000";
    }

    public UnitType default2NewUnit() {
        return UnitType.BOUTIQUE;
    }

    // //////////////////////////////////////

    /**
     * For use by Api and by fixtures.
     */
    @Programmatic
    public FixedAssetRole addRoleIfDoesNotExist(
            final @Named("party") Party party,
            final @Named("type") FixedAssetRoleType type,
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {

        FixedAssetRole role = fixedAssetRoles.findRole(this, party, type, startDate, endDate);
        if (role == null) {
            role = this.createRole(type, party, startDate, endDate);
        }
        return role;
    }

    // //////////////////////////////////////

    private Units<?> unitsRepo;

    public final void injectUnits(final Units<?> unitsRepo) {
        this.unitsRepo = unitsRepo;
    }

    private FixedAssetRoles fixedAssetRoles;

    public final void injectFixedAssetRoles(final FixedAssetRoles fixedAssetRoles) {
        this.fixedAssetRoles = fixedAssetRoles;
    }

    private LocationLookupService locationLookupService;

    public final void injectLocationLookupService(final LocationLookupService locationLookupService) {
        this.locationLookupService = locationLookupService;
    }
}
