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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.RegexValidation;

@DomainService(repositoryFor = Currency.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.4")
public class Currencies extends UdoDomainRepositoryAndFactory<Currency> {

    public Currencies() {
        super(Currencies.class, Currency.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public List<Currency> newCurrency(
            final @ParameterLayout(named = "Reference") @Parameter(regexPattern = RegexValidation.REFERENCE, regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION) String reference,
            final @ParameterLayout(named = "Name") @Parameter(optionality = Optionality.OPTIONAL) String name) {
        findOrCreateCurrency(reference, name);
        return allCurrencies();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Currency> allCurrencies() {
        return allInstances();
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

    @Programmatic
    public Currency findCurrency(final String reference) {
        return uniqueMatch("findByReference", "reference", reference);
    }

    @ActionLayout(hidden = Where.EVERYWHERE)
    public List<Currency> autoComplete(final String searchArg) {
        return allMatches("matchByReferenceOrDescription", "searchArg", searchArg);
    }

}
