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
package org.estatio.module.asset.fixtures.property.builders;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.UnitType;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.base.platform.fake.EstatioFakeDataService;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@Accessors(chain = true)
public final class PropertyAndUnitsAndOwnerAndManagerBuilder
        extends BuilderScriptAbstract<Property, PropertyAndUnitsAndOwnerAndManagerBuilder> {

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private Country country;

    @Getter @Setter
    private PropertyType propertyType;

    @Getter @Setter
    private LocalDate acquireDate;

    @Getter @Setter
    private LocalDate openingDate;

    @Getter @Setter
    private String locationStr;

    @Getter @Setter
    private Party owner;
    @Getter @Setter
    private LocalDate ownerStartDate;
    @Getter @Setter
    private LocalDate ownerEndDate;

    @Getter @Setter
    private Party manager;
    @Getter @Setter
    private LocalDate managerStartDate;
    @Getter @Setter
    private LocalDate managerEndDate;

    @Getter @Setter
    private Integer numberOfUnits;

    @Getter
    private Property object;
    @Getter
    private List<Unit> units = Lists.newArrayList();
    @Getter
    private FixedAssetRole ownerRole;
    @Getter
    private FixedAssetRole managerrole;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        defaultParam("numberOfUnits", executionContext, fakeDataService.values().anInt(10,20));

        final Property property = new PropertyBuilder()
                .setReference(reference)
                .setName(name)
                .setCity(city)
                .setCountry(country)
                .setPropertyType(propertyType)
                .setAcquireDate(acquireDate)
                .setOpeningDate(openingDate)
                .setLocationStr(locationStr)
                .build(this, executionContext)
                .getObject();

        if(owner != null) {
            ownerRole = new PropertyOwnerBuilder()
                    .setProperty(property)
                    .setOwner(owner)
                    .setStartDate(ownerStartDate)
                    .setEndDate(ownerEndDate)
                    .build(this, executionContext)
                    .getObject();
        }

        if(manager != null) {
            managerrole = new PropertyManagerBuilder()
                    .setProperty(property)
                    .setManager(manager)
                    .setStartDate(managerStartDate)
                    .setEndDate(managerEndDate)
                    .build(this, executionContext)
                    .getObject();
        }

        for (int i = 0; i < getNumberOfUnits(); i++) {
            int unitNumber = i + 1;
            final Unit unit = wrap(property)
                    .newUnit(String.format("%s-%03d", property.getReference(), unitNumber), "Unit " + unitNumber,
                            unitType(i));
            unit.setArea(new BigDecimal((i + 1) * 100));
            units.add(unit);
        }

        object = property;
    }

    private UnitType unitType(int n) {
        final UnitType[] unitTypes = UnitType.values();
        return unitTypes[n % unitTypes.length];
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
