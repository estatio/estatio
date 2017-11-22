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
package org.estatio.module.lease.dom;

import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester.FixtureDatumFactory;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetForTesting;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.dom.communicationchannel.CommunicationChannelForTesting;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

public class InvoiceForLease_Test {

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
                    .exercise(new InvoiceForLease());
        }


        @SuppressWarnings({"rawtypes", "unchecked"})
        private static FixtureDatumFactory<InvoiceStatus> statii() {
            return new FixtureDatumFactory(InvoiceStatus.class, (Object[]) InvoiceStatus.values());
        }

    }
}