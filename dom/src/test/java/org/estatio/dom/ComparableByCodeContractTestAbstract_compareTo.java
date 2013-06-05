package org.estatio.dom;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.reflections.Reflections;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.geography.Geography;
import org.estatio.dom.geography.GeographyForTesting;


/**
 * Automatically tests all domain objects implementing {@link ComparableByCode}.
 * 
 * <p>
 * Any that cannot be instantiated are skipped; manually test using
 * {@link ComparableByCodeContractTester}.
 */
public abstract class ComparableByCodeContractTestAbstract_compareTo {

    private final String packagePrefix;
    private Map<Class<?>, Class<?>> noninstantiableSubstitutes;

    protected ComparableByCodeContractTestAbstract_compareTo(String packagePrefix, 
            ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes) {
        this.packagePrefix = packagePrefix;
        this.noninstantiableSubstitutes = noninstantiableSubstitutes;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(packagePrefix);
        
        Set<Class<? extends ComparableByCode>> subtypes = 
                reflections.getSubTypesOf(ComparableByCode.class);
        for (Class<? extends ComparableByCode> subtype : subtypes) {
            if(subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                return;
            }
            subtype = instantiable(subtype);
            test(subtype);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Class<? extends ComparableByCode> instantiable(Class<? extends ComparableByCode> cls) {
        final Class<?> substitute = noninstantiableSubstitutes.get(cls);
        return (Class<? extends ComparableByCode>) (substitute!=null?substitute:cls);
    }

    private <T extends ComparableByCode<T>> void test(Class<T> cls) {
        new ComparableByCodeContractTester<T>(cls).test();
    }

}
