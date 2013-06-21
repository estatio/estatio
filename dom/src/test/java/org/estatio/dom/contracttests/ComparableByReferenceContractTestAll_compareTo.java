package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.ComparableByReference;
import org.estatio.dom.ComparableByReferenceContractTestAbstract_compareTo;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.geography.Geography;
import org.estatio.dom.geography.GeographyForTesting;


/**
 * Automatically tests all domain objects implementing {@link ComparableByReference}.
 */
public class ComparableByReferenceContractTestAll_compareTo extends ComparableByReferenceContractTestAbstract_compareTo {

    public ComparableByReferenceContractTestAll_compareTo() {
        super(Constants.packagePrefix, noninstantiableSubstitutes());
    }

    static ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes() {
        return ImmutableMap.<Class<?>,Class<?>>of(
                Agreement.class, AgreementForTesting.class,
                Geography.class, GeographyForTesting.class);
    }

}
