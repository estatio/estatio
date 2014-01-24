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
package org.estatio.dom.currency;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.EstatioDomainService;

public class Currencies extends EstatioDomainService<Currency> {

    public Currencies() {
        super(Currencies.class, Currency.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "currencies.1")
    public List<Currency> allCurrencies() {
        return allInstances();
    }
    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "currencies.2")
    public List<Currency> newCurrency(
            final @Named("Reference") String reference, 
            final @Named("Name") @Optional String name) {
        createCurrency(reference, name);
        return allCurrencies();
    }

    // //////////////////////////////////////
    
    private Currency createCurrency(final String reference, final String name) {
        final Currency currency = newTransientInstance();
        currency.setReference(reference);
        currency.setName(name);
        persist(currency);
        return currency;
    }

    @Programmatic
    public Currency findCurrency(
            final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }


    @Programmatic
    public List<Currency> autoComplete(final String searchArg) {
        return allMatches("matchByReferenceOrDescription", "searchArg", searchArg);
    }
    
    
}
