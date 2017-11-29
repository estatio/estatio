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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.base.platform.fake.EstatioFakeDataService;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"})
@Accessors(chain = true)
public class PropertyBuilder
        extends BuilderScriptAbstract<PropertyBuilder> {

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

    @Getter
    private Property property;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        defaultParam("reference", executionContext, fakeDataService.values().code(3).toUpperCase());
        defaultParam("name", executionContext, fakeDataService.name().lastName() + " Mall");
        defaultParam("propertyType", executionContext, fakeDataService.collections().anEnum(PropertyType.class));
        defaultParam("city", executionContext, fakeDataService.address().cityPrefix() + " " + fakeDataService.name().lastName() + fakeDataService.address().citySuffix());
        defaultParam("country", executionContext, fakeDataService.collections().aBounded(Country.class));
        defaultParam("acquireDate", executionContext, fakeDataService.dates().before(fakeDataService.periods().days(100, 200)));

        this.property = propertyRepository
                .newProperty(getReference(), getName(), getPropertyType(), getCity(), getCountry(), getAcquireDate());
        property.setOpeningDate(openingDate);
        property.setLocation(Location.fromString(locationStr));


    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    EstatioFakeDataService fakeDataService;

}
