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
package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Countries extends EstatioDomainService<Country> {

    public Countries() {
        super(Countries.class, Country.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Other", sequence = "geography.countries.1")
    public List<Country> allCountries() {
        return allInstances();
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Other", sequence = "geography.countries.2")
    public List<Country> newCountry(
            final @Named("Reference") String reference, 
            final @Named("Alpha-2 Code") String alpha2Code, 
            final @Named("Name") String name) {
        createCountry(reference, alpha2Code, name);
        return allCountries();
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
        if (reference == null) {
            return null;
        }
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }


}
