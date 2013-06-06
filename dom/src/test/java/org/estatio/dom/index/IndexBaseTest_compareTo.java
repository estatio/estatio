package org.estatio.dom.index;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class IndexBaseTest_compareTo extends ComparableContractTest_compareTo<IndexBase> {

    private Index ib1;
    private Index ib2;
    
    @Before
    public void setUp() throws Exception {
        ib1 = new Index();
        ib2 = new Index();
        
        ib1.setReference("A");
        ib2.setReference("B");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<IndexBase>> orderedTuples() {
        return listOf(
                listOf(
                        // reverse order
                        newIndexBase(null, null),
                        newIndexBase(ib1, null),
                        newIndexBase(ib1, null),
                        newIndexBase(ib2, null)
                        ),
                listOf(
                        // reverse order
                        newIndexBase(ib1, null),
                        newIndexBase(ib1, new LocalDate(2012,4,3)),
                        newIndexBase(ib1, new LocalDate(2012,4,3)),
                        newIndexBase(ib1, new LocalDate(2012,3,1))
                        )
                );
    }

    private IndexBase newIndexBase(
            Index index, 
            LocalDate startDate) {
        final IndexBase ib = new IndexBase();
        ib.setIndex(index);
        ib.setStartDate(startDate);
        return ib;
    }

}
