package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexationMethodTest {

    LeaseTermForIndexable term1;
    LeaseTermForIndexable term2;

    @Before
    public void setUp() throws Exception {
        term1 = new LeaseTermForIndexable();
        term2 = new LeaseTermForIndexable();
        term1.setNext(term2);
        term2.setPrevious(term1);

        term1.setBaseIndexStartDate(new LocalDate(2012, 1, 1));
        term1.setNextIndexStartDate(new LocalDate(2013, 1, 1));
        term1.setFrequency(LeaseTermFrequency.YEARLY);
    }

    public static class DoInitialze extends IndexationMethodTest {

        @Test
        public void withBaseIndex() throws Exception {
            //given
            term1.setIndexationMethod(IndexationMethod.BASE_INDEX);
            //when
            term1.initialize();
            term2.initialize();
            //then
            assertThat(term2.getBaseIndexStartDate()).isEqualTo(term1.getBaseIndexStartDate());
            assertThat(term2.getNextIndexStartDate()).isEqualTo(term1.getNextIndexStartDate().plusYears(1));
        }

        @Test
        public void withLastKnownIndex() throws Exception {
            //given
            term1.setIndexationMethod(IndexationMethod.LAST_KNOWN_INDEX);
            //when
            term1.initialize();
            term2.initialize();
            //then
            assertThat(term2.getBaseIndexStartDate()).isEqualTo(term1.getNextIndexStartDate());
            assertThat(term2.getNextIndexStartDate()).isEqualTo(term1.getNextIndexStartDate().plusYears(1));
        }



    }

    public static class DoAlign extends IndexationMethodTest {

        @Test
        public void withBaseIndex() throws Exception {
            //given
            term1.setIndexationMethod(IndexationMethod.BASE_INDEX);
            term1.setBaseValue(new BigDecimal("12.34"));
            term2.setBaseValue(null);
            //when
            term2.doAlign();
            //then
            assertThat(term2.getBaseValue()).isEqualTo(term1.getBaseValue());
        }

        @Test
        public void withBaseIndexAlreadyFilledIn() throws Exception {
            //given
            term1.setIndexationMethod(IndexationMethod.BASE_INDEX);
            term1.setBaseValue(new BigDecimal("12.34"));
            term2.setBaseValue(new BigDecimal("23.45"));
            //when
            term2.doAlign();
            //then
            assertThat(term2.getBaseValue()).isEqualTo(new BigDecimal("23.45"));
        }

        @Test
        public void withLastKnownIndex() throws Exception {
            //given
            term1.setIndexationMethod(IndexationMethod.LAST_KNOWN_INDEX);
            term1.setBaseValue(new BigDecimal("12.34"));
            term1.setSettledValue(new BigDecimal("23.34"));
            //when
            term2.doAlign();
            //then
            assertThat(term2.getBaseValue()).isEqualTo(term1.getSettledValue());
        }

    }




}