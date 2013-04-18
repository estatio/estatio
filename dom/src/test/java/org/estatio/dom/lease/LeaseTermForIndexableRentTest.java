package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.Indices;
import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

public class LeaseTermForIndexableRentTest {

    private LeaseItem item;
    private LeaseTermForIndexableRent term;

    public Index i;

    private IndexBase ib1;
    private IndexBase ib2;
    private IndexValue iv1;
    private IndexValue iv2;

    @Mock
    LeaseTerms mockLeaseTerms;

    @Mock
    Indices mockIndices;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {

        // i = context.getClassUnderTest();
        i = new Index();

        i.setIndexService(mockIndices);

        ib1 = new IndexBase();
        ib1.setStartDate(new LocalDate(2000, 1, 1));
        ib1.setIndex(i);

        i.addToIndexBases(ib1);

        ib2 = new IndexBase();
        ib2.setFactor(BigDecimal.valueOf(1.373));
        ib2.setPreviousBase(ib1);
        ib2.setStartDate(new LocalDate(2011, 1, 1));
        ib1.setNextBase(ib2);
        ib2.setIndex(i);
        i.addToIndexBases(ib2);

        iv1 = new IndexValue();
        iv1.setStartDate(new LocalDate(2010, 1, 1));
        iv1.setIndexBase(ib1);
        iv1.setValue(BigDecimal.valueOf(137.6));
        ib1.addToValues(iv1);

        iv2 = new IndexValue();
        iv2.setStartDate(new LocalDate(2011, 1, 1));
        iv2.setIndexBase(ib2);
        iv2.setValue(BigDecimal.valueOf(101.2));
        ib2.addToValues(iv2);

        item = new LeaseItem();
        item.setType(LeaseItemType.RENT);
        item.setLeaseTermsService(mockLeaseTerms);

        term = new LeaseTermForIndexableRent();
        term.setBaseIndexStartDate(iv1.getStartDate());
        term.setNextIndexStartDate(iv2.getStartDate());
        term.setBaseValue(BigDecimal.valueOf(23456.78));
        term.setIndex(i);
        term.setLeaseItem(item);
        term.setStartDate(new LocalDate(2011, 1, 1));
        term.initialize();
    }

    @Test
    public void updateRunsWell() {
        context.checking(new Expectations() {
            {
                allowing(mockIndices).findIndexValueForDate(with(i), with(new LocalDate(2010,1,1)));
                will(returnValue(iv1));
                allowing(mockIndices).findIndexValueForDate(with(i), with(new LocalDate(2011,1,1)));
                will(returnValue(iv2));
            }
        });
        term.update();
        Assert.assertEquals(new BigDecimal("23691.3500"), term.getIndexedValue());
    }

    @Test
    public void createNextRunsWell() {
        context.checking(new Expectations() {
            {
                allowing(mockLeaseTerms).newLeaseTerm(with(any(LeaseItem.class)), with(any(LeaseTerm.class)));
                will(returnValue(new LeaseTermForIndexableRent()));
            }
        });
        LeaseTermForIndexableRent newTerm = (LeaseTermForIndexableRent) term.createNext();
        newTerm.setPreviousTerm(term);
        newTerm.initialize();
        Assert.assertThat(newTerm.getStartDate(), Is.is(term.getEndDate().plusDays(1)));
        Assert.assertThat(newTerm.getIndex(), Is.is(term.getIndex()));
    }

}
