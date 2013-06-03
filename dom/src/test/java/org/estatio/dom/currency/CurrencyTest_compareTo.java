package org.estatio.dom.currency;

import org.estatio.dom.WithReferenceContractTest_compareTo;


public class CurrencyTest_compareTo extends WithReferenceContractTest_compareTo<Currency> {

    @Override
    protected Currency newWithReference() {
        return new Currency();
    }

}
