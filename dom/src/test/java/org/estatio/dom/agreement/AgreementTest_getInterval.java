package org.estatio.dom.agreement;

import org.estatio.dom.WithIntervalContractTest_getInterval;

public class AgreementTest_getInterval extends WithIntervalContractTest_getInterval<Agreement> {

    protected Agreement newWithInterval() {
        return new AgreementForTesting();
    }
}
