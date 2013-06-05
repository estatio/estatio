package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithIntervalContractTest_compareTo}.
 */
public class WithIntervalContractAutoTest_compareTo{

    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends WithInterval>> subtypes = 
                reflections.getSubTypesOf(WithInterval.class);
        for (Class<? extends WithInterval> subtype : subtypes) {
            try {
                @SuppressWarnings("unused")
                final WithInterval dummy = (WithInterval) subtype.newInstance();
                test(subtype);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private <T extends WithInterval> void test(Class<T> cls) {
        new WithIntervalContractTest_getInterval<T>().with(cls).closedInterval();
    }

}
