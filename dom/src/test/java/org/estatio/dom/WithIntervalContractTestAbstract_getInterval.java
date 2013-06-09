package org.estatio.dom;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import org.junit.Ignore;
import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link WithIntervalContractTester}.
 */
public abstract class WithIntervalContractTestAbstract_getInterval {

    private final String packagePrefix;
    private Map<Class<?>, Class<?>> noninstantiableSubstitutes;

    protected WithIntervalContractTestAbstract_getInterval(String packagePrefix, 
            ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes) {
        this.packagePrefix = packagePrefix;
        this.noninstantiableSubstitutes = noninstantiableSubstitutes;
    }

    @Test
    @Ignore
    public void searchAndTest() {
        Reflections reflections = new Reflections(packagePrefix);
        
        Set<Class<? extends WithInterval>> subtypes = 
                reflections.getSubTypesOf(WithInterval.class);
        for (Class<? extends WithInterval> subtype : subtypes) {
            if(subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                return;
            }
            subtype = instantiable(subtype);
            test(subtype);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private Class<? extends WithInterval> instantiable(Class<? extends WithInterval> cls) {
        final Class<?> substitute = noninstantiableSubstitutes.get(cls);
        return (Class<? extends WithInterval>) (substitute!=null?substitute:cls);
    }

    private <T extends WithInterval> void test(Class<T> cls) {
        new WithIntervalContractTester<T>(cls).test();
    }

}
