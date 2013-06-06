package org.estatio.dom.index;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class IndexValueTest_compareTo extends ComparableContractTest_compareTo<IndexValue> {

    private IndexBase ib1;
    private IndexBase ib2;
    
    @Before
    public void setUp() throws Exception {
        ib1 = new IndexBase();
        ib2 = new IndexBase();
        
        ib1.setStartDate(new LocalDate(2012,4,1));
        ib2.setStartDate(new LocalDate(2012,3,1));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<List<IndexValue>> orderedTuples() {
        return listOf(
                listOf(
                        // natural order
                        newIndexValue(null, null),
                        newIndexValue(ib1, null),
                        newIndexValue(ib1, null),
                        newIndexValue(ib2, null)
                        ),
                listOf(
                        // natural order
                        newIndexValue(ib1, null),
                        newIndexValue(ib1, new LocalDate(2012,4,4)),
                        newIndexValue(ib1, new LocalDate(2012,4,4)),
                        newIndexValue(ib1, new LocalDate(2012,4,3))
                        )
                );
    }

    private IndexValue newIndexValue(
            IndexBase indexBase,
            LocalDate startDate) {
        final IndexValue ib = new IndexValue();
        ib.setIndexBase(indexBase);
        ib.setStartDate(startDate);
        return ib;
    }

}
