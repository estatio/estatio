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
