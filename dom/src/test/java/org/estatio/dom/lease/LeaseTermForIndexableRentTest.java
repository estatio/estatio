package org.estatio.dom.lease;

import java.math.BigDecimal;

import junit.framework.Assert;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.Indices;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LeaseTermForIndexableRentTest {

    private LeaseItem li;
    private LeaseTermForIndexableRent ltfir;
    private Index i;
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

        i = new Index();

        ib1 = new IndexBase();
        ib1.setStartDate(new LocalDate(2000,1,1));
        ib1.setIndex(i);

        i.addToIndexBases(ib1);

        ib2 = new IndexBase();
        ib2.setFactor(BigDecimal.valueOf(1.373));
        ib2.setPreviousBase(ib1);
        ib2.setStartDate(new LocalDate(2011,1,1));
        ib1.setNextBase(ib2);
        ib2.setIndex(i);
        i.addToIndexBases(ib2);

        iv1 = new IndexValue();
        iv1.setStartDate(new LocalDate(2010,1,1));
        iv1.setIndexBase(ib1);
        iv1.setValue(BigDecimal.valueOf(137.6));
        ib1.addToValues(iv1);

        iv2 = new IndexValue();
        iv2.setStartDate(new LocalDate(2011,1,1));
        iv2.setIndexBase(ib2);
        iv2.setValue(BigDecimal.valueOf(101.2));
        ib2.addToValues(iv2);

        li = new LeaseItem();
        li.setType(LeaseItemType.RENT);

        ltfir = new LeaseTermForIndexableRent();
        ltfir.setBaseIndexStartDate(iv1.getStartDate());
        ltfir.setNextIndexStartDate(iv2.getStartDate());
        ltfir.setBaseValue(BigDecimal.valueOf(23456.78));
        ltfir.setIndex(i);
        ltfir.setLeaseItem(li);
        ltfir.setStartDate(new LocalDate(2011,1,1));
        ltfir.setIndexationFrequency(IndexationFrequency.YEARLY);
    }

    @Test
    public void verifyRunsWell() {
        i.setIndexService(mockIndices);
        context.checking(new Expectations() {
            {
                allowing(mockIndices).findIndexValueForDate(with(equal(i)), with(equal(iv1.getStartDate())));
                will(returnValue(iv1));
                allowing(mockIndices).findIndexValueForDate(with(equal(i)), with(equal(iv2.getStartDate())));
                will(returnValue(iv2));
            }
        });
        ltfir.verify();
        Assert.assertEquals(BigDecimal.valueOf(23691.35), ltfir.getIndexedValue());
    }

    @Test
    public void createNewTerm() {
        context.checking(new Expectations() {
            {
                one(mockLeaseTerms).newLeaseTerm(with(equal(li)));
                will(returnValue(new LeaseTermForIndexableRent()));
            }
        });
        ltfir.setLeaseTermsService(mockLeaseTerms);

        LeaseTermForIndexableRent newTerm = ltfir.createNextLeaseTerm();
        Assert.assertEquals(newTerm.getStartDate(), ltfir.getStartDate().plusYears(1));
    }

}
