package org.estatio.dom.lease;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class LeaseUnitTest_compareTo extends ComparableContractTest_compareTo<LeaseUnit> {

    private Lease lease1;
    private Lease lease2;
    
    private UnitForLease unit1;
    private UnitForLease unit2;
    
    @Before
    public void setUp() throws Exception {
        lease1 = new Lease();
        lease1.setReference("ABC");
        
        lease2 = new Lease();
        lease2.setReference("DEF");
        
        unit1 = new UnitForLease();
        unit1.setName("ABC");
        
        unit2 = new UnitForLease();
        unit2.setName("DEF");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<List<LeaseUnit>> orderedTuples() {
        return listOf(
                listOf(
                        newLeaseUnit(null, null, null),
                        newLeaseUnit(lease1, null, null),
                        newLeaseUnit(lease1, null, null),
                        newLeaseUnit(lease2, null, null)
                        ),
                listOf(
                        newLeaseUnit(lease1, null, null),
                        newLeaseUnit(lease1, unit1, null),
                        newLeaseUnit(lease1, unit1, null),
                        newLeaseUnit(lease1, unit2, null)
                        ),
                listOf(
                        newLeaseUnit(lease1, unit1, null),
                        newLeaseUnit(lease1, unit1, new LocalDate(2012,4,1)),
                        newLeaseUnit(lease1, unit1, new LocalDate(2012,4,1)),
                        newLeaseUnit(lease1, unit1, new LocalDate(2012,3,1))
                        )
                );
    }

    private LeaseUnit newLeaseUnit(
            Lease lease,
            UnitForLease unit,
            LocalDate startDate) {
        final LeaseUnit ib = new LeaseUnit();
        ib.setLease(lease);
        ib.setUnit(unit);
        ib.setStartDate(startDate);
        return ib;
    }

}
