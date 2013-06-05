package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithDescription}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithDescriptionContractTest_compareTo}.
 */
public class WithDescriptionContractAutoTest_compareTo{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends WithDescription>> subtypes = 
                reflections.getSubTypesOf(WithDescription.class);
        for (Class<? extends WithDescription> subtype : subtypes) {
            try {
                @SuppressWarnings("unused")
                final WithDescription dummy = (WithDescription) subtype.newInstance();
                test(subtype);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private <T extends WithDescription<T>> void test(Class<T> cls) {
        new WithDescriptionContractTest_compareTo<T>().with(cls).compareAllOrderedTuples();
    }

}
