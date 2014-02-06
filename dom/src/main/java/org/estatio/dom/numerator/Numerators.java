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

import org.estatio.dom.EstatioDomainService;

import org.apache.isis.applib.RecoverableException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

public class Numerators extends EstatioDomainService<Numerator> {

    public Numerators() {
        super(Numerators.class, Numerator.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Administration", sequence = "numerators.1")
    public List<Numerator> allNumerators() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Numerator findGlobalNumerator(
            final @Named("Name") String numeratorName) {
        return findNumerator(numeratorName, null);
    }
    
    @Programmatic
    public Numerator findScopedNumerator(
            final @Named("Name") String numeratorName, 
            final @Named("Scoped to") Object scopedTo) {
        return findNumerator(numeratorName, scopedTo);
    }

    private Numerator findNumerator(final String numeratorName, final Object scopedToIfAny) {
        if(scopedToIfAny == null) {
            return firstMatch("findByName", "name", numeratorName);
        } else {
            final Bookmark bookmark = bookmarkService.bookmarkFor(scopedToIfAny);
            final String objectType = bookmark.getObjectType();
            final String objectIdentifier = bookmark.getIdentifier();
            return firstMatch("findByNameAndObjectTypeAndObjectIdentifier", 
                    "name", numeratorName, 
                    "objectType", objectType, 
                    "objectIdentifier", objectIdentifier);
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public Numerator createGlobalNumerator(
            final String numeratorName, 
            final String format,
            final BigInteger lastIncrement) {

        return findOrCreateNumerator(numeratorName, null, format, lastIncrement);
    }

    @Programmatic
    public Numerator createScopedNumerator(
            final String numeratorName, 
            final Object scopedTo,
            final String format,
            final BigInteger lastIncrement) {

        return findOrCreateNumerator(numeratorName, scopedTo, format, lastIncrement);
    }

    private Numerator findOrCreateNumerator(
            final String numeratorName, 
            final Object scopedToIfAny, 
            final String format, 
            final BigInteger lastIncrement) {

        // validate
        try {
            String.format(format, lastIncrement);
        } catch(Exception ex) {
            throw new RecoverableException("Invalid format string '" + format + "'");
        }

        // existing?
        final Numerator existingIfAny = findNumerator(numeratorName, scopedToIfAny);
        if(existingIfAny != null) {
            String msg = "'" + numeratorName + "' numerator already exists";
            if(scopedToIfAny != null) {
                msg += " for " + getContainer().titleOf(scopedToIfAny);
            }
            getContainer().warnUser(msg);
            return existingIfAny;
        }

        // else create
        return createNumerator(numeratorName, scopedToIfAny, format, lastIncrement);
    }

    private Numerator createNumerator(
            final String numeratorName, 
            final Object scopedToIfAny, 
            final String format, 
            final BigInteger lastIncrement) {
        final Numerator numerator = newTransientInstance();
        numerator.setName(numeratorName);
        if(scopedToIfAny != null) {
            final Bookmark bookmark = bookmarkService.bookmarkFor(scopedToIfAny);
            numerator.setObjectType(bookmark.getObjectType());
            numerator.setObjectIdentifier(bookmark.getIdentifier());
        }
        numerator.setFormat(format);
        numerator.setLastIncrement(lastIncrement);
        persist(numerator);
        return numerator;
    }



    // //////////////////////////////////////

    private BookmarkService bookmarkService;
    public void injectBookmarkService(final BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }
}
