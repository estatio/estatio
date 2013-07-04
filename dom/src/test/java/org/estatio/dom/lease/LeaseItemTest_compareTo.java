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
package org.estatio.dom.lease;

import java.math.BigInteger;
import java.util.List;

import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class LeaseItemTest_compareTo extends ComparableContractTest_compareTo<LeaseItem> {

    private Lease lease1;
    private Lease lease2;
    
    @Before
    public void setUp() throws Exception {
        lease1 = new Lease();
        lease2 = new Lease();
        
        lease1.setReference("A");
        lease2.setReference("B");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<List<LeaseItem>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseItem(null, null, null),
                        newLeaseItem(lease1, null, null),
                        newLeaseItem(lease1, null, null),
                        newLeaseItem(lease2, null, null)
                        ),
                listOf(
                        newLeaseItem(lease1, null, null),
                        newLeaseItem(lease1, LeaseItemType.RENT, null),
                        newLeaseItem(lease1, LeaseItemType.RENT, null),
                        newLeaseItem(lease1, LeaseItemType.SERVICE_CHARGE, null)
                        ),
                listOf(
                        newLeaseItem(lease1, LeaseItemType.RENT, null),
                        newLeaseItem(lease1, LeaseItemType.RENT, 2),
                        newLeaseItem(lease1, LeaseItemType.RENT, 2),
                        newLeaseItem(lease1, LeaseItemType.RENT, 1)
                        )
                );
    }

    private LeaseItem newLeaseItem(
            Lease lease,
            LeaseItemType type, Integer sequence) {
        final LeaseItem li = new LeaseItem();
        li.setLease(lease);
        li.setType(type);
        li.setSequence(sequence!=null?BigInteger.valueOf(sequence.longValue()):null);
        return li;
    }

}
