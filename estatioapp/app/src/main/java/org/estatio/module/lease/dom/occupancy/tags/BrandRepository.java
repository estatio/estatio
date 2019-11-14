/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.module.lease.dom.occupancy.tags;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.apache.isis.applib.query.QueryDefault;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.StringUtils;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;

@DomainService(repositoryFor = Brand.class, nature = NatureOfService.DOMAIN)
public class BrandRepository extends UdoDomainRepositoryAndFactory<Brand> {

    public BrandRepository() {
        super(BrandRepository.class, Brand.class);
    }

    /* TODO: refactor allMatches to use RepositoryService.allMatches() */
    @SuppressWarnings({ "unchecked" })
    @Programmatic
    public List<String> findUniqueNames() {
        List names = allMatches("findUniqueNames");
        return names;
    }

    /* TODO: refactor allMatches to use RepositoryService.allMatches() */
    @SuppressWarnings({ "unchecked" })
    @Programmatic
    public List<String> findUniqueGroups(final String search) {
        if (search == null) return Collections.emptyList();
        List groups = allMatches("findUniqueGroups", "search", search);
        return groups;
    }

    @Programmatic
    public List<BrandGroupViewModel> autoCompleteBrandGroup(final String search) {
        final List<BrandGroupViewModel> groups = findUniqueGroups(search)
                .stream()
                .map(BrandGroupViewModel::new)
                .collect(Collectors.toList());
        groups.add(new BrandGroupViewModel(search));

        return groups;
    }

    @Programmatic
    public Brand findByName(final String name) {
        List<Brand> list = repositoryService.allMatches(new QueryDefault<>(Brand.class,"findByName", "name", name));
        return list.isEmpty() ? null : list.get(0);
    }

    public Brand findUnique(final String name, final ApplicationTenancy applicationTenancy) {
        return repositoryService.uniqueMatch(new QueryDefault<>(Brand.class,
                "findByNameAndAtPath", "name", name, "atPath", applicationTenancy.getPath()));
    }

    @Programmatic
    public List<Brand> matchByName(final String name) {
        return repositoryService.allMatches(new QueryDefault<>(Brand.class,
                "matchByName", "name", StringUtils.wildcardToCaseInsensitiveRegex(name)));
    }

    @Programmatic
    public List<Brand> findByNameLowerCaseAndAppTenancy(final String name, final ApplicationTenancy applicationTenancy) {
        return repositoryService.allMatches(new QueryDefault<>(Brand.class,
                "findByNameLowerCaseAndAppTenancy", "name", name.toLowerCase(), "atPath", applicationTenancy.getPath()));
    }

    public List<Brand> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 0
                ? matchByName("*" + searchPhrase + "*")
                : Lists.<Brand>newArrayList();
    }

    @Programmatic
    public Brand newBrand(
            final String name,
            final BrandCoverage coverage,
            final Country countryOfOrigin,
            final String group,
            final Country countryIfAny) {

        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(countryIfAny);

        return newBrand(name, coverage, countryOfOrigin, group, applicationTenancy);
    }

    @Programmatic
    public Brand newBrand(
            final String name,
            final BrandCoverage coverage,
            final Country countryOfOrigin,
            final String group,
            final ApplicationTenancy applicationTenancy) {
        Brand brand = factoryService.instantiate(Brand.class);
        brand.setName(name);
        brand.setCoverage(coverage);
        brand.setCountryOfOrigin(countryOfOrigin);
        brand.setGroup(group);
        brand.setApplicationTenancyPath(applicationTenancy.getPath());
        repositoryService.persistAndFlush(brand);
        return brand;
    }

    @Programmatic
    public Brand findOrCreate(
            final ApplicationTenancy applicationTenancy,
            final String name,
            final BrandCoverage brandCoverage,
            final Country countryOfOrigin) {
        if (name == null) {
            return null;
        }
        Brand brand = findByName(name);
        if (brand == null) {
            brand = newBrand(name, brandCoverage, countryOfOrigin, null, estatioApplicationTenancyRepository.findCountryTenancyFor(applicationTenancy));
        }
        return brand;
    }

    @Programmatic
    public List<Brand> allBrands() {
        return repositoryService.allInstances(Brand.class);
    }

    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;
}
