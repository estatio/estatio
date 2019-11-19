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
package org.estatio.module.numerator.dom;

import java.math.BigInteger;
import java.util.List;

import org.apache.isis.applib.RecoverableException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Numerator.class)
public class NumeratorRepository extends UdoDomainRepositoryAndFactory<Numerator> {

    public NumeratorRepository() {
        super(NumeratorRepository.class, Numerator.class);
    }

    public List<Numerator> allNumerators() {
        return repositoryService.allInstances(Numerator.class);
    }

    public Numerator find(
            final String name,
            final Country countryIfAny,
            final Object objectIfAny,
            final Object object2IfAny) {

        if(countryIfAny == null) {
            return findByName(name);
        }
        if(objectIfAny == null) {
            return findByNameAndCountry(name, countryIfAny);
        }
        if (object2IfAny == null) {
            return findByNameAndCountryAndObject(name, countryIfAny, objectIfAny);
        }
        return findByNameAndCountryAndObjectAndObject2(name, countryIfAny, objectIfAny, object2IfAny);
    }

    private Numerator findByName(final String name) {
        List<Numerator> list = repositoryService.allMatches(new QueryDefault<>(Numerator.class,
                "findByName",
                "name", name));
        return list.isEmpty() ? null : list.get(0);
    }

    private Numerator findByNameAndCountry(
            final String name,
            final Country country) {
        List<Numerator> list = repositoryService.allMatches(new QueryDefault<>(Numerator.class,
                "findByNameAndCountry",
                "name", name,
                "country", country));
        return list.isEmpty() ? null : list.get(0);
    }

    private Numerator findByNameAndCountryAndObject(
            final String name,
            final Country country,
            final Object object) {

        final Bookmark bookmark = bookmarkService.bookmarkFor(object);
        final String objectType = bookmark.getObjectType();
        final String objectIdentifier = bookmark.getIdentifier();

        List<Numerator> list = repositoryService.allMatches(new QueryDefault<>(Numerator.class,
                "findByNameAndCountryAndObject", "name", name,
                "country", country,
                "objectType", objectType,
                "objectIdentifier", objectIdentifier));
        return list.isEmpty() ? null : list.get(0);
    }

    private Numerator findByNameAndCountryAndObjectAndObject2(
            final String name,
            final Country country,
            final Object object,
            final Object object2) {

        final Bookmark bookmark = bookmarkService.bookmarkFor(object);
        final String objectType = bookmark.getObjectType();
        final String objectIdentifier = bookmark.getIdentifier();

        final Bookmark bookmark2 = getBookmarkService().bookmarkFor(object2);
        final String objectType2 = bookmark2.getObjectType();
        final String objectIdentifier2 = bookmark2.getIdentifier();

        List<Numerator> list = repositoryService.allMatches(new QueryDefault<>(Numerator.class,
                "findByNameAndCountryAndObjectAndObject2", "name", name,
                "country", country,
                "objectType", objectType,
                "objectIdentifier", objectIdentifier,
                "objectType2", objectType2,
                "objectIdentifier2", objectIdentifier2));
        return list.isEmpty() ? null : list.get(0);
    }

    public Numerator findOrCreate(
            final String name,
            final Country countryIfAny,
            final Object objectIfAny,
            final Object object2IfAny,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {

        final Numerator existingIfAny = find(name, countryIfAny, objectIfAny, object2IfAny);
        if(existingIfAny != null) {
            return existingIfAny;
        }

        try {
            String.format(format, lastIncrement);
        } catch(Exception ex) {
            throw new RecoverableException(String.format("Invalid format string '%s'", format));
        }

        return create(name, countryIfAny, objectIfAny, object2IfAny, format, lastIncrement, applicationTenancy);
    }


    public Numerator create(
            final String name,
            final Country countryIfAny,
            final Object objectIfAny,
            final Object object2IfAny,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {

        final Numerator numerator = new Numerator(name, countryIfAny, applicationTenancy.getPath(), format, lastIncrement);
        if(objectIfAny != null) {
            final Bookmark bookmark = bookmarkService.bookmarkFor(objectIfAny);
            numerator.setObjectType(bookmark.getObjectType());
            numerator.setObjectIdentifier(bookmark.getIdentifier());
        }
        if(object2IfAny != null) {
            final Bookmark bookmark2 = bookmarkService.bookmarkFor(object2IfAny);
            numerator.setObjectType2(bookmark2.getObjectType());
            numerator.setObjectIdentifier2(bookmark2.getIdentifier());
        }

        return repositoryService.persistAndFlush(numerator);
    }


}
