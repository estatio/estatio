package org.estatio.dom.index;

import java.util.List;

import org.joda.time.LocalDate;

import org.estatio.dom.ComparableContractTest_compareTo;


public class IndexBaseTest_compareTo extends ComparableContractTest_compareTo<IndexBase> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<IndexBase>> orderedTuples() {
        return listOf(
                listOf(
                        // reverse order
                        newIndexBase(new LocalDate(2012,4,4)),
                        newIndexBase(new LocalDate(2012,4,3)),
                        newIndexBase(new LocalDate(2012,4,3)),
                        newIndexBase(new LocalDate(2012,3,1))
                        )
                );
    }

    private IndexBase newIndexBase(
            LocalDate startDate) {
        final IndexBase ib = new IndexBase();
        ib.setStartDate(startDate);
        return ib;
    }

}
