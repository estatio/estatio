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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.geography.Country;
import org.estatio.dom.utils.StringUtils;

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

    @Programmatic
    public List<Brand> matchByName(final String name) {
        return allMatches("matchByName", "name", StringUtils.wildcardToCaseInsensitiveRegex(name));
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
            brand = brandMenu.newBrand(name, brandCoverage, countryOfOrigin, null, applicationTenancy);
        }
        return brand;
    }

    @Inject
    private BrandMenu brandMenu;
}
