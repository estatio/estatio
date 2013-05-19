package org.estatio.dom.lease;

import org.estatio.dom.lease.UnitForLease;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForLeaseableAssets {

    public static FixtureDatumFactory<UnitForLease> unitsForLease() {
        UnitForLease unit1 = new UnitForLease();
        unit1.setName("Unit 1");
        UnitForLease unit2 = new UnitForLease();
        unit2.setName("Unit 2");
        return new FixtureDatumFactory<UnitForLease>(UnitForLease.class, unit1, unit2);
    }

}
