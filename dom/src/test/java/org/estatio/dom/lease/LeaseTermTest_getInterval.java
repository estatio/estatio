package org.estatio.dom.lease;

import org.estatio.dom.WithIntervalContractTest_getInterval;

public class LeaseTermTest_getInterval extends WithIntervalContractTest_getInterval<LeaseTerm> {

    protected LeaseTerm newWithInterval() {
        return new LeaseTerm();
    }
}
