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
package org.estatio.dom.lease.tags;

import java.util.List;

import javax.jdo.Query;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Hidden
public class Brands extends EstatioDomainService<Brand> {

    public Brands() {
        super(Brands.class, Brand.class);
    }

    // //////////////////////////////////////

    @SuppressWarnings({ "unchecked" })
    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<String> findUniqueNames() {
        final Query query = newQuery("SELECT name FROM org.estatio.dom.lease.tags.Brand");
        return (List<String>) query.execute();
    }

    @Hidden
    public Brand findByName(final String name) {
        return firstMatch("findByName", "name", name);
    }

    @Hidden
    public List<Brand> matchByName(final String name) {
        return allMatches("matchByName", "name", StringUtils.wildcardToCaseInsensitiveRegex(name));
    }

    @Programmatic
    public Brand findOrCreate(final String name) {
        if (name == null) {
            return null;
        }
        Brand brand = findByName(name);
        if (brand == null) {
            brand = newTransientInstance(Brand.class);
            brand.setName(name);
        }
        return brand;
    }

    @Hidden
    public List<Brand> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 0
                ? matchByName("*" + searchPhrase + "*")
                : Lists.<Brand> newArrayList();
    }

}
