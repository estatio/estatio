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
package org.estatio.fixture;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.domlink.Link;

public class EstatioRefDataTeardownFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteAllIndex();
    }

    protected void deleteAllIndex() {

        deleteFrom(Currency.class);

        deleteFrom(State.class);
        deleteFrom(Country.class);

        deleteFrom(Charge.class);
        deleteFrom(ChargeGroup.class);

        deleteFrom(TaxRate.class);
        deleteFrom(Tax.class);

        deleteFrom(IndexValue.class);
        deleteFrom(IndexBase.class);
        deleteFrom(Index.class);

        deleteFrom(Link.class);

    }

    protected void deleteFrom(final Class cls) {
        preDeleteFrom(cls);
        deleteFrom(cls.getSimpleName());
        postDeleteFrom(cls);
    }

    protected void deleteFrom(final String table) {
        isisJdoSupport.executeUpdate("DELETE FROM " + "\"" + table + "\"");
    }

    protected void preDeleteFrom(final Class cls) {}

    protected void postDeleteFrom(final Class cls) {}

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
