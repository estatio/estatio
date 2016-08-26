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
package org.estatio.dom.lease.tags;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.geography.Country;

@DomainService(repositoryFor = Brand.class, nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.9"
)
public class BrandMenu extends UdoDomainRepositoryAndFactory<Brand> {

    public BrandMenu() {
        super(BrandMenu.class, Brand.class);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    public Brand newBrand(
            final String name,
            final @Parameter(optionality = Optionality.OPTIONAL) BrandCoverage coverage,
            final @Parameter(optionality = Optionality.OPTIONAL) Country countryOfOrigin,
            final @Parameter(optionality = Optionality.OPTIONAL) String group,
            final ApplicationTenancy applicationTenancy
    ) {
        return brandRepository.newBrand(name, coverage, countryOfOrigin, group, applicationTenancy);
    }

    public List<String> choices3NewBrand() {
        return brandRepository.findUniqueGroups();
    }

    public List<ApplicationTenancy> choices4NewBrand() {
        return estatioApplicationTenancyRepository.countryTenanciesIncludeGlobalIfTenancyIsGlobalForCurrentUser();
    }

    public ApplicationTenancy default4NewBrand() {
        return Dflt.of(choices4NewBrand());
    }

    public String validateNewBrand(
            final String name,
            final BrandCoverage coverage,
            final Country countryOfOrigin,
            final String group,
            final ApplicationTenancy applicationTenancy) {
        return brandRepository.validateNewBrand(name, coverage, countryOfOrigin, group, applicationTenancy);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Brand> allBrands() {
        return brandRepository.allBrands();
    }

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    public BrandRepository brandRepository;

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private MeService meService;

}
