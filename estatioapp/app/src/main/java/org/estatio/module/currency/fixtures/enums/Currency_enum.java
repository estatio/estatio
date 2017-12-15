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
package org.estatio.module.currency.fixtures.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.currency.fixtures.builders.CurrencyBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum Currency_enum implements PersonaWithBuilderScript<Currency,CurrencyBuilder>, PersonaWithFinder<Currency> {

    EUR("EUR","Euro"),
    SEK("SEK", "Swedish krona"),
    GBP("GBP", "Pound sterling"),
    USD("USD", "US dollar");

    private final String reference;
    private final String name;

    @Override
    public Currency findUsing(final ServiceRegistry2 serviceRegistry) {
        final CurrencyRepository currencyRepository = serviceRegistry.lookupService(CurrencyRepository.class);
        return currencyRepository.findCurrency(reference);
    }

    @Override
    public CurrencyBuilder builder() {
        return new CurrencyBuilder()
                .setReference(reference)
                .setName(name);
    }

    public static class PersistAll extends FixtureScript {

        @Override
        protected void execute(ExecutionContext executionContext) {
            for (final Currency_enum datum : values()) {
                executionContext.executeChildT(this, datum.builder());
            }
        }


    }
}
