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

import java.math.BigInteger;
import java.util.List;

import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class LeaseTermTest_compareTo extends ComparableContractTest_compareTo<LeaseTerm> {

    private LeaseItem leaseItem1;
    private LeaseItem leaseItem2;
    
    @Before
    public void setUp() throws Exception {
        leaseItem1 = new LeaseItem();
        leaseItem2 = new LeaseItem();
        
        leaseItem1.setType(LeaseItemType.RENT);
        leaseItem2.setType(LeaseItemType.SERVICE_CHARGE);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<List<LeaseTerm>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseTerm(null, null),
                        newLeaseTerm(leaseItem1, null),
                        newLeaseTerm(leaseItem1, null),
                        newLeaseTerm(leaseItem2, null)
                        ),
                listOf(
                        newLeaseTerm(leaseItem1, null),
                        newLeaseTerm(leaseItem1, 1),
                        newLeaseTerm(leaseItem1, 1),
                        newLeaseTerm(leaseItem1, 2)
                        )
                );
    }

    private LeaseTerm newLeaseTerm(
            LeaseItem leaseItem, Integer sequence) {
        final LeaseTerm lt = new LeaseTermForTesting();
        lt.setLeaseItem(leaseItem);
        lt.setSequence(sequence != null? BigInteger.valueOf(sequence.longValue()): null);
        return lt;
    }
    
}
