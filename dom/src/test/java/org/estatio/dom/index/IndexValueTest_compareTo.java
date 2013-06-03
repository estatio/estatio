package org.estatio.dom.index;

import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.dom.ComparableContractTest_compareTo;


public class IndexValueTest_compareTo extends ComparableContractTest_compareTo<IndexValue> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<IndexValue>> orderedTuples() {
        return listOf(
                listOf(
                        // natural order
                        newIndexValue(null),
                        newIndexValue(new LocalDate(2012,4,3)),
                        newIndexValue(new LocalDate(2012,4,3)),
                        newIndexValue(new LocalDate(2012,4,4))
                        )
                );
    }

    private IndexValue newIndexValue(
            LocalDate startDate) {
        final IndexValue ib = new IndexValue();
        ib.setStartDate(startDate);
        return ib;
    }

}
