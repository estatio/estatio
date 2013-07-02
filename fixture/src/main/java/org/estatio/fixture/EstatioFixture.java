/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.fixtures.AbstractFixture;

import org.estatio.fixture.agreement.AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.charge.ChargeFixture;
import org.estatio.fixture.charge.CurrencyFixture;
import org.estatio.fixture.geography.GeographyFixture;
import org.estatio.fixture.index.IndexFixture;
import org.estatio.fixture.invoice.InvoiceFixture;
import org.estatio.fixture.lease.LeasesFixture;
import org.estatio.fixture.party.PartiesFixture;
import org.estatio.fixture.tax.TaxFixture;


public class EstatioFixture extends AbstractFixture {

    public EstatioFixture() {
    }
    
    @Override
    public void install() {
        
        List<AbstractFixture> fixtures = Arrays.asList(
            newFixture(GeographyFixture.class),
            newFixture(AgreementTypesAndRoleTypesAndCommunicationChannelTypesFixture.class),
            newFixture(TaxFixture.class),
            newFixture(CurrencyFixture.class),
            newFixture(ChargeFixture.class),
            newFixture(IndexFixture.class),
            newFixture(PartiesFixture.class),
            newFixture(PropertiesAndUnitsFixture.class),
            newFixture(LeasesFixture.class),
            newFixture(InvoiceFixture.class)
        );

        for (AbstractFixture fixture : fixtures) {
            fixture.install(); 
            getContainer().flush();
        }

    }

    private AbstractFixture newFixture(Class<? extends AbstractFixture> fixtureClass) {
        return getContainer().newTransientInstance(fixtureClass);
    }

}
