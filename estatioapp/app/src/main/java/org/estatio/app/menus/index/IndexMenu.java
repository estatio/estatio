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
package org.estatio.app.menus.index;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Dflt;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.country.CountryServiceForCurrentUser;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.index.IndexMenu"
)
@DomainServiceLayout(
        named = "Indices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "60.2"
)
public class IndexMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Index newIndex(
            @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION)
            final String reference,
            final String name,
            final Country country) {

        final ApplicationTenancy applicationTenancy =  estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);
        return indexRepository.newIndex(reference, name, applicationTenancy);
    }

    public List<Country> choices2NewIndex() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public Country default2NewIndex() {
        return Dflt.of(choices2NewIndex());
    }




    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "60.3")
    public List<Index> allIndices() {
        return indexRepository.all();
    }



    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;


    @Inject
    private IndexRepository indexRepository;
}
