package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithName}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithNameContractTest_compareTo}.
 */
public class WithNameContractAutoTest_compareTo{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends WithName>> subtypes = 
                reflections.getSubTypesOf(WithName.class);
        for (Class<? extends WithName> subtype : subtypes) {
            try {
                @SuppressWarnings("unused")
                final WithName dummy = subtype.newInstance();
                test(subtype);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private <T extends WithName<T>> void test(Class<T> cls) {
        new WithNameContractTest_compareTo<T>().with(cls).compareAllOrderedTuples();
    }

}
