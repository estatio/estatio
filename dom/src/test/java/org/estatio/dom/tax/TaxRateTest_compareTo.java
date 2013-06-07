package org.estatio.dom.tax;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class TaxRateTest_compareTo extends ComparableContractTest_compareTo<TaxRate> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<TaxRate>> orderedTuples() {
        return listOf(
                listOf(
                        newTaxRate(null),
                        newTaxRate(new LocalDate(2012,4,2)),
                        newTaxRate(new LocalDate(2012,4,2)),
                        newTaxRate(new LocalDate(2012,3,1))
                        )
                );
    }

    private TaxRate newTaxRate(
            LocalDate startDate) {
        final TaxRate tr = new TaxRate();
        tr.setStartDate(startDate);
        return tr;
    }

}
