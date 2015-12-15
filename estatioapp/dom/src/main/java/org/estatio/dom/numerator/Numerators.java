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
package org.estatio.dom.numerator;

import java.math.BigInteger;
import java.util.List;

import org.apache.isis.applib.RecoverableException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(menuOrder = "80", repositoryFor = Numerator.class)
@DomainServiceLayout(
        named = "Administration",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "120.1"
)
public class Numerators extends UdoDomainRepositoryAndFactory<Numerator> {

    public Numerators() {
        super(Numerators.class, Numerator.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<Numerator> allNumerators() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Numerator findGlobalNumerator(
            final String numeratorName,
            final ApplicationTenancy applicationTenancy) {
        return findNumerator(numeratorName, null, applicationTenancy);
    }
    
    @Programmatic
    public Numerator findScopedNumerator(
            final String numeratorName,
            final Object scopedTo,
            final ApplicationTenancy applicationTenancy) {
        return findNumerator(numeratorName, scopedTo, applicationTenancy);
    }

    private Numerator findNumerator(final String numeratorName, final Object scopedToIfAny, final ApplicationTenancy applicationTenancy) {
        if(scopedToIfAny == null) {
            return firstMatch("findByNameAndApplicationTenancyPath", "name", numeratorName, "applicationTenancyPath", applicationTenancy.getPath());
        } else {
            final Bookmark bookmark = getBookmarkService().bookmarkFor(scopedToIfAny);
            final String objectType = bookmark.getObjectType();
            final String objectIdentifier = bookmark.getIdentifier();
            return firstMatch("findByNameAndObjectTypeAndObjectIdentifierAndApplicationTenancyPath",
                    "name", numeratorName, 
                    "objectType", objectType, 
                    "objectIdentifier", objectIdentifier,
                    "applicationTenancyPath" ,applicationTenancy.getPath());
        }
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

    private Numerator findOrCreateNumerator(
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

        // existing?
        final Numerator existingIfAny = findNumerator(numeratorName, scopedToIfAny, applicationTenancy);
        if(existingIfAny != null) {
            String msg = "'" + numeratorName + "' numerator already exists";
            if(scopedToIfAny != null) {
                msg += " for " + getContainer().titleOf(scopedToIfAny);
            }
            getContainer().warnUser(msg);
            return existingIfAny;
        }

        // else create
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
