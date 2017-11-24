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
package org.estatio.module.asset.fixtures;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.UnitType;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.base.platform.fake.EstatioFakeDataService;
import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;

@Accessors(chain = true)
public class PropertyBuilder extends BuilderScriptAbstract<PropertyBuilder> {

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
    private Party manager;

    @Getter @Setter
    private Integer numberOfUnits;

    @Getter @Setter
    private Property property;

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
        property.setOpeningDate(openingDate);
        if(locationStr != null) {
            property.setLocation(Location.fromString(locationStr));
        }

        property.addRoleIfDoesNotExist(owner, FixedAssetRoleTypeEnum.PROPERTY_OWNER, ld(1999, 1, 1), ld(2000, 1, 1));
        property.addRoleIfDoesNotExist(manager, FixedAssetRoleTypeEnum.ASSET_MANAGER, null, null);

//        if(getOwner() != null) {
//            wrap(property).newRole(FixedAssetRoleTypeEnum.PROPERTY_OWNER, getOwner(), getAcquireDate(), null);
//        }
//        if(getManager() != null) {
//            wrap(property).newRole(FixedAssetRoleTypeEnum.ASSET_MANAGER, getManager(), getAcquireDate(), null);
//        }

        for (int i = 0; i < getNumberOfUnits(); i++) {
            int unitNumber = i + 1;
            wrap(property).newUnit(String.format("%s-%03d", reference, unitNumber), "Unit " + unitNumber, unitType(i)).setArea(new BigDecimal((i + 1) * 100));

//            final String unitRef = buildUnitReference(property.getReference(), i);
//            final UnitType unitType = fakeDataService.collections().anEnum(UnitType.class);
//            final String unitName = fakeDataService.name().firstName();
//            wrap(property).newUnit(unitRef, unitName, unitType);
        }
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
