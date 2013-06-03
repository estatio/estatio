package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.dom.ComparableContractTest_compareTo;


public class LeaseTermTest_compareTo extends ComparableContractTest_compareTo<LeaseTerm> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<LeaseTerm>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseTermForIndexableRent(null),
                        newLeaseTermForServiceCharge(null),
                        newLeaseTermForServiceCharge(null),
                        newLeaseTermForTurnoverRent(null)
                        ),
                listOf(
                        newLeaseTermForIndexableRent(null),
                        newLeaseTermForIndexableRent(new LocalDate(2012,3,1)),
                        newLeaseTermForIndexableRent(new LocalDate(2012,3,1)),
                        newLeaseTermForIndexableRent(new LocalDate(2012,3,2))
                        ),
                listOf(
                        newLeaseTermForServiceCharge(null),
                        newLeaseTermForServiceCharge(new LocalDate(2012,3,1)),
                        newLeaseTermForServiceCharge(new LocalDate(2012,3,1)),
                        newLeaseTermForServiceCharge(new LocalDate(2012,3,2))
                        ),
                listOf(
                        newLeaseTermForTurnoverRent(null),
                        newLeaseTermForTurnoverRent(new LocalDate(2012,3,1)),
                        newLeaseTermForTurnoverRent(new LocalDate(2012,3,1)),
                        newLeaseTermForTurnoverRent(new LocalDate(2012,3,2))
                        )
                );
    }

    private LeaseTerm newLeaseTermForIndexableRent(
            LocalDate startDate) {
        final LeaseTerm lt = new LeaseTermForIndexableRent();
        lt.setStartDate(startDate);
        return lt;
    }
    
    private LeaseTerm newLeaseTermForServiceCharge(
            LocalDate startDate) {
        final LeaseTerm lt = new LeaseTermForServiceCharge();
        lt.setStartDate(startDate);
        return lt;
    }

    private LeaseTerm newLeaseTermForTurnoverRent(
            LocalDate startDate) {
        final LeaseTerm lt = new LeaseTermForTurnoverRent();
        lt.setStartDate(startDate);
        return lt;
    }

}
