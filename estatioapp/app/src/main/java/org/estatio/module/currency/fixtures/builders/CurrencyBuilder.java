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
package org.estatio.module.currency.fixtures.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"reference"}, callSuper = false)
@ToString(of={"reference"})
@Accessors(chain = true)
public class CurrencyBuilder extends BuilderScriptAbstract<Currency, CurrencyBuilder> {

    @Getter @Setter
    private String reference;
    @Getter @Setter
    private String name;

    @Getter
    private Currency object;

    public static final String EUR = "EUR";
    public static final String SEK = "SEK";
    public static final String GBP = "GBP";
    public static final String USD = "USD";

    @Override
    protected void execute(ExecutionContext executionContext) {
        createCurrency(EUR, "Euro", executionContext);
        createCurrency(SEK, "Swedish krona", executionContext);
        createCurrency(GBP, "Pound sterling", executionContext);
        createCurrency(USD, "US dollar", executionContext);
    }

    private void createCurrency(String reference, String name, ExecutionContext executionContext) {
        final Currency currency = currencyRepository.findOrCreateCurrency(reference, name);
        executionContext.addResult(this, currency.getReference(), currency);
    }

    @Inject
    CurrencyRepository currencyRepository;

}
