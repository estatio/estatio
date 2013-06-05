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
 * Automatically tests all domain objects implementing {@link ComparableByDescription}.
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
        
        Set<Class<? extends ComparableByDescription>> subtypes = 
                reflections.getSubTypesOf(ComparableByDescription.class);
        for (Class<? extends ComparableByDescription> subtype : subtypes) {
            if(subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                return;
            }
            subtype = instantiable(subtype);
            test(subtype);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Class<? extends ComparableByDescription> instantiable(Class<? extends ComparableByDescription> cls) {
        final Class<?> substitute = noninstantiableSubstitutes.get(cls);
        return (Class<? extends ComparableByDescription>) (substitute!=null?substitute:cls);
    }

    private <T extends ComparableByDescription<T>> void test(Class<T> cls) {
        new ComparableByDescriptionContractTester<T>(cls).test();
    }

}
