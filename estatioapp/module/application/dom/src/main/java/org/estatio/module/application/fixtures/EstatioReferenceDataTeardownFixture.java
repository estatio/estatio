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
package org.estatio.module.application.fixtures;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexBase;
import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.link.dom.Link;
import org.estatio.module.tax.EstatioTaxModule;

public class EstatioReferenceDataTeardownFixture extends FixtureScript {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        deleteFrom(Currency.class);

        deleteFrom(State.class);
        deleteFrom(Country.class);

        deleteFrom(Charge.class);
        deleteFrom(ChargeGroup.class);

        executionContext.executeChild(this, new EstatioTaxModule.Teardown());

        deleteFrom(IndexValue.class);
        deleteFrom(IndexBase.class);
        deleteFrom(Index.class);

        deleteFrom(Link.class);

    }

    protected void deleteFrom(final Class cls) {
        preDeleteFrom(cls);
        doDeleteFrom(cls);
        postDeleteFrom(cls);
    }

    private void doDeleteFrom(final Class cls) {
        isisJdoSupport.deleteAll(cls);
    }

    protected void preDeleteFrom(final Class cls) {}

    protected void postDeleteFrom(final Class cls) {}

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
