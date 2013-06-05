package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithTitle}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithTitleContractTest_compareTo}.
 */
public class WithTitleContractAutoTest_compareTo{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends WithTitle>> subtypes = 
                reflections.getSubTypesOf(WithTitle.class);
        for (Class<? extends WithTitle> subtype : subtypes) {
            try {
                @SuppressWarnings("unused")
                final WithTitle dummy = subtype.newInstance();
                test(subtype);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private <T extends WithTitle<T>> void test(Class<T> cls) {
        new WithTitleContractTest_compareTo<T>().with(cls).compareAllOrderedTuples();
    }

}
