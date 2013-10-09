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
package org.estatio.dom.contracttests;

import java.util.Map;

import com.google.common.collect.Maps;

import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalContractTestAbstract_getInterval;
import org.estatio.dom.WithIntervalContractTester.WIInstantiator;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannel;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceItemForTesting;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.lease.Occupancy;

/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 */
public class WithIntervalContractTestAll_getInterval extends WithIntervalContractTestAbstract_getInterval {

    @SuppressWarnings("rawtypes")
    private static Map<Class, WIInstantiator> map() {
        Map<Class, WIInstantiator> map = Maps.newLinkedHashMap();
        map.put(Agreement.class,
                new WIInstantiator<AgreementForTesting>(AgreementForTesting.class));
        map.put(AgreementRole.class,
                new WIInstantiator<AgreementRole>(AgreementRole.class) {
                    public AgreementRole newWithIntervalWithParent() throws Exception {
                        AgreementRole lt = newWithInterval();
                        lt.setAgreement(new AgreementForTesting());
                        return lt;
                    };
                });
        map.put(AgreementRoleCommunicationChannel.class,
                new WIInstantiator<AgreementRoleCommunicationChannel>(AgreementRoleCommunicationChannel.class) {
            public AgreementRoleCommunicationChannel newWithIntervalWithParent() throws Exception {
                AgreementRoleCommunicationChannel lt = newWithInterval();
                lt.setRole(new AgreementRole());
                return lt;
            };
        });
        map.put(InvoiceItem.class,
                new WIInstantiator<InvoiceItemForTesting>(InvoiceItemForTesting.class));
        map.put(Occupancy.class,
                new WIInstantiator<Occupancy>(Occupancy.class) {
                    public Occupancy newWithIntervalWithParent() throws Exception {
                        Occupancy lt = newWithInterval();
                        lt.setLease(new Lease());
                        return lt;
                    };
                });
        map.put(LeaseTerm.class,
                new WIInstantiator<LeaseTermForTesting>(LeaseTermForTesting.class) {
            public LeaseTermForTesting newWithIntervalWithParent() throws Exception {
                LeaseTermForTesting lt = newWithInterval();
                final LeaseItem li = new LeaseItem();
                lt.setLeaseItem(li);
                li.setLease(new Lease());
                return lt;
            };
        });
        map.put(LeaseItem.class,
                new WIInstantiator<LeaseItem>(LeaseItem.class) {
            public LeaseItem newWithIntervalWithParent() throws Exception {
                LeaseItem lt = newWithInterval();
                lt.setLease(new Lease());
                return lt;
            };
        });
        map.put(Unit.class,
                new WIInstantiator<Unit>(Unit.class));
        return map;
    }

    public WithIntervalContractTestAll_getInterval() {
        super(Constants.packagePrefix, map());
    }

}
