/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.fixturescripts;

import java.math.BigInteger;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.numerator.Numerator;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

public class CreateInvoiceNumerators extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        for (Property property : properties.allProperties()) {
            final Numerator numerator = invoices.createInvoiceNumberNumerator(property, property.getReference().concat("-%04d"), BigInteger.ZERO);
            fixtureResults.add(this, property.getReference(), numerator);
        }
    }

    // //////////////////////////////////////

    private Invoices invoices;

    final public void setInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }

    private Properties properties;

    final public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

}
