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
package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.RegexValidation;

@DomainService(repositoryFor = Country.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.5"
)
public class Countries extends EstatioDomainService<Country> {

    public Countries() {
        super(Countries.class, Country.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public List<Country> newCountry(
            final @ParameterLayout(named="Reference") @RegEx(validation = RegexValidation.REFERENCE, caseSensitive = true) String reference,
            final @ParameterLayout(named="Alpha-2 Code") String alpha2Code,
            final @ParameterLayout(named="Name") String name) {
        createCountry(reference, alpha2Code, name);
        return allCountries();
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Country> allCountries() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Country createCountry(
            final String reference,
            final String alpha2Code,
            final String name) {
        final Country country = newTransientInstance();
        country.setReference(reference);
        country.setAlpha2Code(alpha2Code);
        country.setName(name);
        persist(country);
        return country;
    }

    @Programmatic
    public Country findCountry(
            final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

}
