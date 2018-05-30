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
package org.estatio.module.countryapptenancy.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.app.user.MeService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.countryapptenancy.dom.util.AtPathUtils;

@DomainService(nature = NatureOfService.DOMAIN)
public class CountryServiceForCurrentUser {

    @Programmatic
    public List<Country> countriesForCurrentUser() {
        final String currentUserAtPath = meService.me().getFirstAtPathUsingSeparator(';');
        final String currentUserCountryRef = AtPathUtils.toCountryRefRegex(currentUserAtPath);
        return countryRepository.findCountries(currentUserCountryRef);
    }

    @Inject
    private CountryRepository countryRepository;
    @Inject
    private MeService meService;

}
