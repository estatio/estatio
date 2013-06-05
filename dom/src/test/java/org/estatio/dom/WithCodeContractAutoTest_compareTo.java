package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithCode}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithCodeContractTest_compareTo}.
 */
public class WithCodeContractAutoTest_compareTo{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends WithCode>> subtypes = 
                reflections.getSubTypesOf(WithCode.class);
        for (Class<? extends WithCode> subtype : subtypes) {
            try {
                @SuppressWarnings("unused")
                final WithCode dummy = (WithCode) subtype.newInstance();
                test(subtype);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private <T extends WithCode<T>> void test(Class<T> cls) {
        new WithCodeContractTest_compareTo<T>().with(cls).compareAllOrderedTuples();
    }

}
