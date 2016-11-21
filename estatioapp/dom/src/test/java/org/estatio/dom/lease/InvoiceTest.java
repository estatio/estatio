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
package org.estatio.dom.lease;

import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester.FixtureDatumFactory;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.communicationchannel.CommunicationChannelForTesting;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class InvoiceTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(statii())
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .withFixture(pojos(Currency.class))
                    .withFixture(pojos(Lease.class))
                    .withFixture(pojos(FixedAsset.class, FixedAssetForTesting.class))
                    .withFixture(pojos(BankMandate.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .withFixture(pojos(CommunicationChannel.class, CommunicationChannelForTesting.class))
                    .exercise(new Invoice());
        }


        @SuppressWarnings({"rawtypes", "unchecked"})
        private static FixtureDatumFactory<InvoiceStatus> statii() {
            return new FixtureDatumFactory(InvoiceStatus.class, (Object[]) InvoiceStatus.values());
        }

    }
}