package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class LeaseItemTest_compareTo extends ComparableContractTest_compareTo<LeaseItem> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<LeaseItem>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseItem(null, null),
                        newLeaseItem(LeaseItemType.RENT, null),
                        newLeaseItem(LeaseItemType.RENT, null),
                        newLeaseItem(LeaseItemType.SERVICE_CHARGE, null)
                        ),
                listOf(
                        newLeaseItem(LeaseItemType.RENT, null),
                        newLeaseItem(LeaseItemType.RENT, new LocalDate(2012,3,1)),
                        newLeaseItem(LeaseItemType.RENT, new LocalDate(2012,3,1)),
                        newLeaseItem(LeaseItemType.RENT, new LocalDate(2012,3,2))
                        )
                );
    }

    private LeaseItem newLeaseItem(
            LeaseItemType type,
            LocalDate startDate) {
        final LeaseItem ib = new LeaseItem();
        ib.setType(type);
        ib.setStartDate(startDate);
        return ib;
    }

}
