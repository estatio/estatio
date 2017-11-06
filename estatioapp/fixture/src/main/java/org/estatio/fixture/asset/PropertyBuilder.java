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
package org.estatio.fixture.asset;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.asset.UnitType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.fixture.EstatioFakeDataService;

public class PropertyBuilder extends FixtureScript {

    //region > reference
    private String reference;
    public String getReference() {
        return reference;
    }
    public void setReference(final String reference) {
        this.reference = reference;
    }
    //endregion

    //region > name
    private String name;
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    //endregion

    //region > city
    private String city;
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }
    //endregion

    //region > country
    private Country country;

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }
    //endregion

    //region > propertyType
    private PropertyType propertyType;
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final PropertyType propertyType) {
        this.propertyType = propertyType;
    }
    //endregion

    //region > acquireDate
    private LocalDate acquireDate;
    public LocalDate getAcquireDate() {
        return acquireDate;
    }

    public void setAcquireDate(final LocalDate acquireDate) {
        this.acquireDate = acquireDate;
    }
    //endregion

    //region > owner
    private Party owner;
    public Party getOwner() {
        return owner;
    }
    public void setOwner(final Party owner) {
        this.owner = owner;
    }
    //endregion

    //region > manager
    private Party manager;
    public Party getManager() {
        return manager;
    }
    public void setManager(final Party manager) {
        this.manager = manager;
    }
    //endregion

    //region > numberOfUnits
    private Integer numberOfUnits;
    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }
    public void setNumberOfUnits(final Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }
    //endregion



    //region > property (output property)
    private Property property;
    public Property getProperty() {
        return property;
    }
    public void setProperty(final Property property) {
        this.property = property;
    }
    //endregion


    @Override
    protected void execute(final ExecutionContext executionContext) {

        defaultParam("reference", executionContext, fakeDataService.values().code(3).toUpperCase());
        defaultParam("name", executionContext, fakeDataService.name().lastName() + " Mall");
        defaultParam("propertyType", executionContext, fakeDataService.collections().anEnum(PropertyType.class));
        defaultParam("city", executionContext, fakeDataService.address().cityPrefix() + " " + fakeDataService.name().lastName() + fakeDataService.address().citySuffix());
        defaultParam("country", executionContext, fakeDataService.collections().aBounded(Country.class));
        defaultParam("acquireDate", executionContext, fakeDataService.dates().before(fakeDataService.periods().days(100, 200)));

        defaultParam("numberOfUnits", executionContext, fakeDataService.values().anInt(10,20));

        final ApplicationTenancy countryApplicationTenancy = applicationTenancyRepository.findByPath("/" + getCountry().getReference());

        this.property = propertyRepository
                .newProperty(getReference(), getName(), getPropertyType(), getCity(), getCountry(), getAcquireDate());

        if(getOwner() != null) {
            wrap(property).newRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, getOwner(), getAcquireDate(), null);
        }
        if(getManager() != null) {
            wrap(property).newRole(FixedAssetRoleTypeEnum.ASSET_MANAGER, getManager(), getAcquireDate(), null);
        }

        for (int i = 0; i < getNumberOfUnits(); i++) {
            final String unitRef = buildUnitReference(property.getReference(), i);
            final UnitType unitType = fakeDataService.collections().anEnum(UnitType.class);
            final String unitName = fakeDataService.name().firstName();
            wrap(property).newUnit(unitRef, unitName, unitType);
        }
    }

    String buildUnitReference(final String propertyReference, final Integer unitNum) {
        return String.format("%1$s-%2$03d", propertyReference, unitNum);
    }

    // //////////////////////////////////////

    @Inject
    protected StateRepository stateRepository;

    @Inject
    protected CountryRepository countryRepository;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected UnitRepository unitRepository;

    @Inject
    protected PartyRepository partyRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    EstatioFakeDataService fakeDataService;


}
