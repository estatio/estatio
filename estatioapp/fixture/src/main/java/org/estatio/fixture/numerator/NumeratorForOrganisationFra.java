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
package org.estatio.fixture.numerator;

import javax.inject.Inject;

import org.incode.module.country.dom.impl.CountryRepository;
import org.estatio.dom.party.PartyConstants;
import org.incode.module.country.fixture.CountriesRefData;

public class NumeratorForOrganisationFra extends NumeratorForOrganisationAbstract {

    public static final String REF = "ACME_NL";

    @Override
    protected void execute(ExecutionContext executionContext) {
        createNumeratorForOrganisation(
                PartyConstants.ORGANISATION_REFERENCE_NUMERATOR_NAME,
                "FRCL%04d",
                countryRepository.findCountry(CountriesRefData.FRA),
                executionContext);
    }

    @Inject
    CountryRepository countryRepository;

}
