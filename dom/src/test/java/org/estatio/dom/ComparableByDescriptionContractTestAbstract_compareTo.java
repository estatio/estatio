package org.estatio.dom;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all domain objects implementing {@link WithDescriptionComparable}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link ComparableByDescriptionContractTester}.
 */
public abstract class ComparableByDescriptionContractTestAbstract_compareTo {

    private final String packagePrefix;
    private Map<Class<?>, Class<?>> noninstantiableSubstitutes;

    protected ComparableByDescriptionContractTestAbstract_compareTo(String packagePrefix, 
            ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes) {
        this.packagePrefix = packagePrefix;
        this.noninstantiableSubstitutes = noninstantiableSubstitutes;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(packagePrefix);
        
        Set<Class<? extends WithDescriptionComparable>> subtypes = 
                reflections.getSubTypesOf(WithDescriptionComparable.class);
        for (Class<? extends WithDescriptionComparable> subtype : subtypes) {
            if(subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                return;
            }
            subtype = instantiable(subtype);
            test(subtype);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Class<? extends WithDescriptionComparable> instantiable(Class<? extends WithDescriptionComparable> cls) {
        final Class<?> substitute = noninstantiableSubstitutes.get(cls);
        return (Class<? extends WithDescriptionComparable>) (substitute!=null?substitute:cls);
    }

    private <T extends WithDescriptionComparable<T>> void test(Class<T> cls) {
        new ComparableByDescriptionContractTester<T>(cls).test();
    }

}
