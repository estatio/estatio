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

package org.estatio.dom.lease.tags;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.dom.geography.Country;
import org.incode.module.base.dom.utils.StringUtils;

@DomainService(repositoryFor = Brand.class, nature = NatureOfService.DOMAIN)
public class BrandRepository extends UdoDomainRepositoryAndFactory<Brand> {

    public BrandRepository() {
        super(BrandRepository.class, Brand.class);
    }

    @SuppressWarnings({ "unchecked" })
    @Programmatic
    public List<String> findUniqueNames() {
        List names = allMatches("findUniqueNames");
        return names;
    }

    @SuppressWarnings({ "unchecked" })
    @Programmatic
    public List<String> findUniqueGroups() {
        List groups = allMatches("findUniqueGroups");
        return groups;
    }

    @Programmatic
    public Brand findByName(final String name) {
        return firstMatch("findByName", "name", name);
    }

    public Brand findUnique(final String name, final ApplicationTenancy applicationTenancy) {
        return uniqueMatch("findByNameAndAtPath", "name", name, "atPath", applicationTenancy.getPath());
    }

    @Programmatic
    public List<Brand> matchByName(final String name) {
        return allMatches("matchByName", "name", StringUtils.wildcardToCaseInsensitiveRegex(name));
    }

    @Programmatic
    public List<Brand> findByNameLowerCaseAndAppTenancy(final String name, final ApplicationTenancy applicationTenancy) {
        return allMatches("findByNameLowerCaseAndAppTenancy", "name", name.toLowerCase(), "atPath", applicationTenancy.getPath());
    }

    public List<Brand> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 0
                ? matchByName("*" + searchPhrase + "*")
                : Lists.<Brand>newArrayList();
    }

    public Brand newBrand(
            final String name,
            final @Parameter(optionality = Optionality.OPTIONAL) BrandCoverage coverage,
            final @Parameter(optionality = Optionality.OPTIONAL) Country countryOfOrigin,
            final @Parameter(optionality = Optionality.OPTIONAL) String group,
            final ApplicationTenancy applicationTenancy) {
        Brand brand;
        brand = newTransientInstance(Brand.class);
        brand.setName(name);
        brand.setCoverage(coverage);
        brand.setCountryOfOrigin(countryOfOrigin);
        brand.setGroup(group);
        brand.setApplicationTenancyPath(applicationTenancy.getPath());
        persist(brand);
        return brand;
    }

    public String validateNewBrand(
            final String name,
            final BrandCoverage coverage,
            final Country countryOfOrigin,
            final String group,
            final ApplicationTenancy applicationTenancy) {
        if (findByNameLowerCaseAndAppTenancy(name, applicationTenancy).size() > 0) {
            return String.format("Brand with name %s exists already for %s", name, applicationTenancy.getName());
        }
        return null;
    }

    @Programmatic
    public Brand findOrCreate(
            final ApplicationTenancy applicationTenancy,
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL) final BrandCoverage brandCoverage,
            @Parameter(optionality = Optionality.OPTIONAL) final Country countryOfOrigin) {
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
        return allInstances();
    }

    @Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
