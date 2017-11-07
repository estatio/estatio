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
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.bookmark.Bookmark;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Numerator.class)
public class NumeratorRepository extends UdoDomainRepositoryAndFactory<Numerator> {

    public NumeratorRepository() {
        super(NumeratorRepository.class, Numerator.class);
    }

    /**
     * Required for integration testing.
     *
     * <p>
     *     When running integration tests for just this module, the Isis metamodel validation (as of 1.13.1) requires
     *     that there is at least one entity found.  Such tests only have a single service - this repository - to
     *     locate entities (transitively), and all the other methods in this service are @Programmatic, meaning that
     *     they are ignored.  This dummy method therefore ensures that the Numerator class is found, ensuring that
     *     the validation error does not trip up.
     * </p>
     */
    @Action(hidden = Where.EVERYWHERE)
    public Numerator registerInMetaModel() {
        return null;
    }


    @Programmatic
    public List<Numerator> allNumerators() {
        return allInstances();
    }



    @Programmatic
    public Numerator findGlobalNumerator(
            final String numeratorName,
            final ApplicationTenancy applicationTenancy) {
        return findNumerator(numeratorName, null, applicationTenancy);
    }

    @Programmatic
    public Numerator findScopedNumeratorIncludeWildCardMatching(
            final String numeratorName,
            final Object scopedTo,
            final ApplicationTenancy applicationTenancy) {
        Numerator result = findNumerator(numeratorName, scopedTo, applicationTenancy);
        return result == null ? findFirstNumeratorForObjectTypeMatchingAppTenancyPath(numeratorName, scopedTo, applicationTenancy) : result;
    }


    @Programmatic
    public Numerator findNumerator(final String numeratorName, final Object scopedToIfAny, final ApplicationTenancy applicationTenancy) {
        if(scopedToIfAny == null) {
            return firstMatch("findByNameAndApplicationTenancyPath",
                    "name", numeratorName,
                    "applicationTenancyPath", applicationTenancy == null ? "/" : applicationTenancy.getPath());
        } else {
            final Bookmark bookmark = getBookmarkService().bookmarkFor(scopedToIfAny);
            final String objectType = bookmark.getObjectType();
            final String objectIdentifier = bookmark.getIdentifier();
            return firstMatch("findByNameAndObjectTypeAndObjectIdentifierAndApplicationTenancyPath",
                    "name", numeratorName, 
                    "objectType", objectType, 
                    "objectIdentifier", objectIdentifier,
                    "applicationTenancyPath", applicationTenancy == null ? "/" : applicationTenancy.getPath());
        }
    }

    private Numerator findFirstNumeratorForObjectTypeMatchingAppTenancyPath(final String numeratorName, final Object scopedToIfAny, final ApplicationTenancy applicationTenancy){
        final Bookmark bookmark = getBookmarkService().bookmarkFor(scopedToIfAny);
        final String objectType = bookmark.getObjectType();
        return firstMatch("findByNameAndObjectTypeAndApplicationTenancyPath",
                "name", numeratorName,
                "objectType", objectType,
                "applicationTenancyPath", applicationTenancy == null ? "/" : applicationTenancy.getPath());
    }

    // //////////////////////////////////////

    @Programmatic
    public Numerator createGlobalNumerator(
            final String numeratorName,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {

        return findOrCreateNumerator(numeratorName, null, format, lastIncrement, applicationTenancy);
    }

    @Programmatic
    public Numerator createScopedNumerator(
            final String numeratorName,
            final Object scopedTo,
            final String format,
            final BigInteger lastIncrement, final ApplicationTenancy applicationTenancy) {

        return findOrCreateNumerator(numeratorName, scopedTo, format, lastIncrement, applicationTenancy);
    }

    @Programmatic
    public Numerator findOrCreateNumerator(
            final String numeratorName,
            final Object scopedToIfAny,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {

        // validate
        try {
            String.format(format, lastIncrement);
        } catch(Exception ex) {
            throw new RecoverableException("Invalid format string '" + format + "'");
        }

        final Numerator existingIfAny = findNumerator(numeratorName, scopedToIfAny, applicationTenancy);
        if(existingIfAny != null) {
            return existingIfAny;
        }
        return createNumerator(numeratorName, scopedToIfAny, format, lastIncrement, applicationTenancy);
    }

    private Numerator createNumerator(
            final String numeratorName,
            final Object scopedToIfAny,
            final String format,
            final BigInteger lastIncrement,
            final ApplicationTenancy applicationTenancy) {
        final Numerator numerator = newTransientInstance();
        numerator.setName(numeratorName);
        numerator.setApplicationTenancyPath(applicationTenancy.getPath());
        if(scopedToIfAny != null) {
            final Bookmark bookmark = getBookmarkService().bookmarkFor(scopedToIfAny);
            numerator.setObjectType(bookmark.getObjectType());
            numerator.setObjectIdentifier(bookmark.getIdentifier());
        }
        numerator.setFormat(format);
        numerator.setLastIncrement(lastIncrement);
        persist(numerator);
        return numerator;
    }


}
