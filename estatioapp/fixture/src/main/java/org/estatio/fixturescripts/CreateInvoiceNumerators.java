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
package org.estatio.fixturescripts;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.CollectionNumerators;
import org.estatio.dom.numerator.Numerator;

import static org.estatio.integtests.VT.bi;

public class CreateInvoiceNumerators extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        for (Property property : properties.allProperties()) {
            final Numerator numerator = collectionNumerators.createInvoiceNumberNumerator(property, property.getReference().concat("-%04d"), bi(0));
            fixtureResults.addResult(this, property.getReference(), numerator);
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    CollectionNumerators collectionNumerators;

    @javax.inject.Inject
    Properties properties;

}
