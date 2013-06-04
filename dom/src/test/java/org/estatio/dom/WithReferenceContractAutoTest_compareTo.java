package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithReference}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithReferenceContractTest_compareTo}.
 */
public class WithReferenceContractAutoTest_compareTo{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends WithReference>> subtypes = 
                reflections.getSubTypesOf(WithReference.class);
        for (Class<? extends WithReference> subtype : subtypes) {
            try {
                @SuppressWarnings("unused")
                final WithReference dummy = subtype.newInstance();
                test(subtype);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private <T extends WithReference<T>> void test(Class<T> cls) {
        new WithReferenceContractTest_compareTo<T>().with(cls).compareAllOrderedTuples();
    }

}
