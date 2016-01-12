package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.index.IndexationService;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexationMethodTest {

    LeaseTermForIndexable term1;
    LeaseTermForIndexable term2;

    @Before
    public void setUp() throws Exception {
        term1 = new LeaseTermForIndexable();
        term1.initialize();
        term1.setBaseIndexStartDate(new LocalDate(2012, 1, 1));
        term1.setNextIndexStartDate(new LocalDate(2013, 1, 1));
        term1.setFrequency(LeaseTermFrequency.YEARLY);

        term2 = new LeaseTermForIndexable();
        term1.setNext(term2);
        term2.setPrevious(term1);
        term2.indexationService = new IndexationService();

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
            term2.initialize();
            term2.doAlign();
            //then
            assertThat(term2.getBaseValue()).isEqualTo(term1.getBaseValue());
        }

        @Test
        public void withBaseIndexAlreadyFilledIn() throws Exception {
            //given
            term1.setIndexationMethod(IndexationMethod.BASE_INDEX);
            term1.setBaseValue(new BigDecimal("12.34"));
            //when
            term2.initialize();
            term2.setBaseValue(new BigDecimal("23.45"));
            term2.doAlign();
            //then
            assertThat(term2.getBaseValue()).isEqualTo(new BigDecimal("23.45"));
        }

        @Test
        public void withLastKnownIndex() throws Exception {
            // indexation method, base value, settled value, expected base value
            tester(IndexationMethod.LAST_KNOWN_INDEX, new BigDecimal("12.34"), new BigDecimal("23.34"), new BigDecimal("23.34"));
            tester(IndexationMethod.LAST_KNOWN_INDEX, null, new BigDecimal("23.34"), new BigDecimal("23.34"));
        }

        private void tester(IndexationMethod indexationMethod, BigDecimal baseValue, BigDecimal settledValue, BigDecimal expectedBaseValue){
            term1.setIndexationMethod(indexationMethod);
            term1.setBaseValue(baseValue);
            term1.setSettledValue(settledValue);
            //when
            term2.initialize();
            term2.doAlign();
            assertThat(term2.getBaseValue()).isEqualTo(expectedBaseValue);
        }
    }
}