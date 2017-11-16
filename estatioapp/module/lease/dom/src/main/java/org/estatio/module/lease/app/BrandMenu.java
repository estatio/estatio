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
package org.estatio.module.lease.app;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.base.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;

@DomainService(
        repositoryFor = Brand.class,
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.brand.BrandMenu"
)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "900.9"
)
public class BrandMenu extends UdoDomainRepositoryAndFactory<Brand> {

    public BrandMenu() {
        super(BrandMenu.class, Brand.class);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    public Brand newBrand(
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BrandCoverage coverage,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Country countryOfOrigin,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String group,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(describedAs = "Leave blank for a global brand")
            final Country country
    ) {
        return brandRepository.newBrand(name, coverage, countryOfOrigin, group, country);
    }

    public List<String> choices3NewBrand() {
        return brandRepository.findUniqueGroups();
    }


    public String validateNewBrand(
            final String name,
            final BrandCoverage coverage,
            final Country countryOfOrigin,
            final String group,
            final Country countryIfAny) {

        final String atPath = meService.me().getAtPath();
        final ApplicationTenancyLevel userAtPath = ApplicationTenancyLevel.of(atPath);
        if(countryIfAny == null && (userAtPath == null || !userAtPath.isRoot())) {
            return "You may only create country-specific brands";
        }

        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(countryIfAny);
        if (brandRepository.findByNameLowerCaseAndAppTenancy(name, applicationTenancy).size() > 0) {
            return String.format("Brand with name %s exists already for %s", name, applicationTenancy.getName());
        }

        return null;
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Brand> findBrand(final String search) {
        return brandRepository.matchByName(search);
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public List<Brand> allBrands() {
        return brandRepository.allBrands();
    }

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    public BrandRepository brandRepository;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    MeService meService;

}
