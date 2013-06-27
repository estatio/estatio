package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.WithNameComparable;
import org.estatio.dom.ComparableByNameContractTestAbstract_compareTo;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;


/**
 * Automatically tests all domain objects implementing {@link WithNameComparable}.
 */
public class ComparableByNameContractTestAll_compareTo extends ComparableByNameContractTestAbstract_compareTo {

    public ComparableByNameContractTestAll_compareTo() {
        super(Constants.packagePrefix, noninstantiableSubstitutes());
    }

    static ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes() {
        return ImmutableMap.<Class<?>,Class<?>>of(
                FixedAsset.class, FixedAssetForTesting.class,
                Party.class, PartyForTesting.class);
    }

}
