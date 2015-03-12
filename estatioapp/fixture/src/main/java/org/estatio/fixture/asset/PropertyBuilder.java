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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.asset.UnitType;
import org.estatio.dom.asset.Units;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioFixtureScript;

public class PropertyBuilder extends EstatioFixtureScript {

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

        defaultParam("reference", executionContext, faker().values().code(3).toUpperCase());
        defaultParam("name", executionContext, faker().name().lastName() + " Mall");
        defaultParam("propertyType", executionContext, faker().collections().anEnum(PropertyType.class));
        defaultParam("city", executionContext, faker().address().cityPrefix() + " " + faker().name().lastName() + faker().address().citySuffix());
        defaultParam("country", executionContext, faker().collections().aBounded(Country.class));
        defaultParam("acquireDate", executionContext, faker().dates().before(faker().periods().days(100, 200)));

        defaultParam("numberOfUnits", executionContext, faker().values().anInt(10,20));

        final ApplicationTenancy countryApplicationTenancy = applicationTenancies.findTenancyByPath("/" + getCountry().getReference());

        this.property = properties.newProperty(getReference(), getName(), getPropertyType(), getCity(), getCountry(), getAcquireDate(), countryApplicationTenancy);

        if(getOwner() != null) {
            wrap(property).newRole(FixedAssetRoleType.PROPERTY_OWNER, getOwner(), getAcquireDate(), null);
        }
        if(getManager() != null) {
            wrap(property).newRole(FixedAssetRoleType.ASSET_MANAGER, getManager(), getAcquireDate(), null);
        }

        for (int i = 0; i < getNumberOfUnits(); i++) {
            final String unitRef = buildUnitReference(property.getReference(), i);
            final UnitType unitType = faker().collections().anEnum(UnitType.class);
            final String unitName = faker().name().firstName();
            wrap(units).newUnit(property, unitRef, unitName, unitType);
        }

    }

    String buildUnitReference(final String propertyReference, final Integer unitNum) {
        return String.format("%1$s-%2$03d", propertyReference, unitNum);
    }

    // //////////////////////////////////////

    @Inject
    protected States states;

    @Inject
    protected Countries countries;

    @Inject
    protected Properties properties;

    @Inject
    protected Units units;

    @Inject
    protected Parties parties;

    @Inject
    protected ApplicationTenancies applicationTenancies;

}
