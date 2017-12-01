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

import org.isisaddons.module.fakedata.dom.FakeDataService;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.PropertyType;
import org.estatio.module.base.platform.fake.EstatioFakeDataService;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@ToString(of={"reference"})
@Accessors(chain = true)
public final class PropertyBuilder
        extends BuilderScriptAbstract<Property,PropertyBuilder> {

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
    private Property object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        defaultParam("reference", executionContext, fakeDataService2.strings().fixedUpper(3).toUpperCase());
        defaultParam("name", executionContext, fakeDataService.name().lastName() + " Mall");
        defaultParam("propertyType", executionContext, fakeDataService.enums().anyOf(PropertyType.class));
        defaultParam("city", executionContext, fakeDataService.addresses().cityPrefix() + " " + fakeDataService.name().lastName() + fakeDataService.addresses().citySuffix());
        defaultParam("country", executionContext, fakeDataService.collections().anyBounded(Country.class));
        defaultParam("acquireDate", executionContext, fakeDataService2.dates().before(fakeDataService2.periods().days(100, 200)));

        this.object = propertyRepository
                .newProperty(getReference(), getName(), getPropertyType(), getCity(), getCountry(), getAcquireDate());
        object.setOpeningDate(openingDate);
        object.setLocation(Location.fromString(locationStr));


    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    FakeDataService fakeDataService;
    @Inject
    EstatioFakeDataService fakeDataService2;

}
