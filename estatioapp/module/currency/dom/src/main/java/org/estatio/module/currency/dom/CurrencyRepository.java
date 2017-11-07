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

package org.estatio.module.currency.dom;

import java.util.List;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = Currency.class)
public class CurrencyRepository extends UdoDomainRepositoryAndFactory<Currency> {

    public CurrencyRepository() {
        super(CurrencyRepository.class, Currency.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Currency> allCurrencies() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public Currency findCurrency(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public Currency findOrCreateCurrency(final String reference, final String name) {
        Currency currency = findCurrency(reference);
        if (currency == null) {
            currency = newTransientInstance();
            currency.setReference(reference);
            currency.setName(name);
            persist(currency);
        }
        return currency;
    }

    // //////////////////////////////////////

    @ActionLayout(hidden = Where.EVERYWHERE)
    public List<Currency> autoComplete(final String searchArg) {
        return allMatches("matchByReferenceOrDescription", "searchArg", searchArg);
    }

}
