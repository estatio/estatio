package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.index.Indexable;
import org.estatio.dom.lease.indexation.IndexationMethod;
import org.estatio.dom.lease.indexation.IndexationService;

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

    public static class DoInitialize extends IndexationMethodTest {

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
        public void testBaseAndEffectiveIndexedValue() throws Exception {
            // indexation method, base value, indexed value, effective indexed value, settled value, expected base value, expected effective indexed value
            // with settled value
            tester(IndexationMethod.BASE_INDEX, "3.00", "2.00", "4.00", "1.00", "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "3.00", "2.00", "4.00", "1.00", "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "3.00", "2.00", "4.00", "1.00", "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "3.00", "2.00", "4.00", "1.00", "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "3.00", "2.00", "4.00", "1.00", "3.00", "4.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, "3.00", "2.00", "4.00", "1.00", "1.00", "4.00");

            tester(IndexationMethod.BASE_INDEX, "-3.00", "-2.00", "-4.00", "-1.00", "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "-3.00", "-2.00", "-4.00", "-1.00", "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "-3.00", "-2.00", "-4.00", "-1.00", "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "-3.00", "-2.00", "-4.00", "-1.00", "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "-3.00", "-2.00", "-4.00", "-1.00", "-3.00", "-4.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, "-3.00", "-2.00", "-4.00", "-1.00", "-1.00", "-4.00");

            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, null, "1.00", "1.00", "1.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, null, "-1.00", "-1.00", "-1.00");

            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, "2.00", "1.00", "1.00", "2.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, "-2.00", "-1.00", "-1.00", "-2.00");

            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, "1.00", "2.00", "2.00", "2.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, "-1.00", "-2.00", "-2.00", "-2.00");

            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, "2.00", null, "2.00", "2.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, "-2.00", null, "-2.00", "-2.00");

            // no settled value
            tester(IndexationMethod.LAST_KNOWN_INDEX, null, null, null, null, "0", "0");

            tester(IndexationMethod.BASE_INDEX, "3.00", "4.00", "5.00", null, "3.00", "5.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "3.00", "4.00", "5.00", null, "3.00", "5.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "3.00", "4.00", "5.00", null, "3.00", "5.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "3.00", "4.00", "5.00", null, "3.00", "5.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "3.00", "4.00", "5.00", null, "3.00", "5.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, "3.00", "4.00", "5.00", null, "5.00", "5.00");

            tester(IndexationMethod.BASE_INDEX, "-3.00", "4.00", "-5.00", null, "-3.00", "-5.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "-3.00", "-4.00", "-5.00", null, "-3.00", "-5.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "-3.00", "-4.00", "-5.00", null, "-3.00", "-5.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "-3.00", "-4.00", "-5.00", null, "-3.00", "-5.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "-3.00", "-4.00", "-5.00", null, "-3.00", "-5.00");
            tester(IndexationMethod.LAST_KNOWN_INDEX, "-3.00", "-4.00", "-5.00", null, "-5.00", "-5.00");

            // effective indexed value = effective indexed value previous overrides indexed value previous
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "3.00", "5.00", "4.00", null, "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "3.00", "5.00", "4.00", null, "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "3.00", "5.00", "4.00", null, "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "3.00", "2.00", "1.00", null, "3.00", "1.00");

            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "-3.00", "-5.00", "-4.00", null, "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "-3.00", "-5.00", "-4.00", null, "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "-3.00", "-2.00", "-1.00", null, "-3.00", "-1.00");

            // effective indexed value = max [base value , first non zero (previous effective value, previous indexed value)]
            // Nb. term.indexedValue is not calculated here; only previous indexed value
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "3.00", "2.00", "1.00", null, "3.00", "3.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "3.00", "4.00", null, null, "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "3.00", "5.00", "4.00", null, "3.00", "4.00");

            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "-3.00", "-2.00", "-1.00", null, "-3.00", "-3.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "-3.00", "-4.00", null, null, "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "-3.00", "-5.00", "-4.00", null, "-3.00", "-4.00");

            // effective indexed value = max baseValue, (indexedValue not calc here), first non-zero (effective indexed value previous, indexed value previous)
            tester(IndexationMethod.BASE_INDEX, "3.00", "5.00", "4.00", null, "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "3.00", "5.00", "4.00", null, "3.00", "4.00");
            tester(IndexationMethod.BASE_INDEX, "3.00", "2.00", "1.00", null, "3.00", "3.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "3.00", "2.00", "1.00", null, "3.00", "3.00");

            tester(IndexationMethod.BASE_INDEX, "-3.00", "-5.00", "-4.00", null, "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "-3.00", "-5.00", "-4.00", null, "-3.00", "-4.00");
            tester(IndexationMethod.BASE_INDEX, "-3.00", "-2.00", "-1.00", null, "-3.00", "-3.00");
            tester(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "-3.00", "-2.00", "-1.00", null, "-3.00", "-3.00");

        }

        void tester(IndexationMethod indexationMethod, String baseValue, String indexedValue, String effectiveIndexedValue, String settledValue, String expectedBaseValue, String expectedEffectiveIndexedValue){
            //given
            term1.setIndexationMethod(indexationMethod);
            term1.setBaseValue(Util.bdFromStr(baseValue));
            term1.setIndexedValue(Util.bdFromStr(indexedValue));
            term1.setEffectiveIndexedValue(Util.bdFromStr(effectiveIndexedValue));
            term1.setSettledValue(Util.bdFromStr(settledValue));
            //when
            term2.initialize();
            term2.doAlign();
            //then
            assertThat(term2.getBaseValue()).isEqualTo(Util.bdFromStr(expectedBaseValue));
            assertThat(term2.getEffectiveIndexedValue()).isEqualTo(Util.bdFromStr(expectedEffectiveIndexedValue));

        }

    }


    public static class DoAlignWithChangingBaseRentNegativeIndexation extends IndexationMethodTest {

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
            term2.indexationService = new IndexationService(){
                @Override
                public void indexate(final Indexable input){
                    term2.setIndexedValue(new BigDecimal("12.34"));
                }
            };

        }

        @Test
        public void testBaseAndEffectiveIndexValue(){
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "12.34", "13.45", "13.45", "23.45", "12.34");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "12.34", "13.45", "13.45", "23.45", "12.34");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX, "12.34", "13.45", "13.45", "23.45", "23.45");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "12.34", "13.45", "13.45", "23.45", "23.45");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "12.34", "13.45", "13.45", "23.45", "23.45");

        }
    }

    public static class DoAlignWithChangingBaseRentPositiveIndexation extends IndexationMethodTest {

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
            term2.indexationService = new IndexationService(){
                @Override
                public void indexate(final Indexable input){
                    term2.setIndexedValue(new BigDecimal("43.21"));
                }
            };

        }

        @Test
        public void testBaseAndEffectiveIndexValue(){
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_ALLOW_DECREASE, "12.34", "13.45", "13.45", "23.45", "43.21");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_FRANCE, "12.34", "13.45", "13.45", "23.45", "43.21");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX, "12.34", "13.45", "13.45", "23.45", "43.21");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_ALLOW_DECREASE_BASE_AS_FLOOR_FRANCE, "12.34", "13.45", "13.45", "23.45", "43.21");
            testerWithChangingBaseValue(IndexationMethod.BASE_INDEX_NO_DECREASE_FRANCE, "12.34", "13.45", "13.45", "23.45", "43.21");

        }

    }

    void testerWithChangingBaseValue(IndexationMethod indexationMethod, String baseValue, String indexedValue, String effectiveIndexedValue, String newBaseValue, String expectedEffectiveIndexedValue){
        //given
        term1.setIndexationMethod(indexationMethod);
        term1.setBaseValue(Util.bdFromStr(baseValue));
        term1.setIndexedValue(Util.bdFromStr(indexedValue));
        term1.setEffectiveIndexedValue(Util.bdFromStr(effectiveIndexedValue));
        //when
        term2.initialize();
        term2.setBaseValue(Util.bdFromStr(newBaseValue));
        term2.doAlign();
        //then
        assertThat(term2.getBaseValue()).isEqualTo(Util.bdFromStr(newBaseValue));
        assertThat(term2.getEffectiveIndexedValue()).isEqualTo(Util.bdFromStr(expectedEffectiveIndexedValue));

    }


    static class Util {
        static BigDecimal bdFromStr(String input){
            return input == null ? null : new BigDecimal(input);
        }
    }


}