package org.estatio.dom;

import com.google.common.collect.ImmutableMap;

import org.apache.isis.core.unittestsupport.bidir.BidirectionalRelationshipContractTestAbstract;
import org.apache.isis.core.unittestsupport.bidir.Instantiator;
import org.apache.isis.core.unittestsupport.bidir.InstantiatorSimple;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.geography.Geography;
import org.estatio.dom.geography.GeographyForTesting;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class BidirectionalRelationshipContractTestAll extends BidirectionalRelationshipContractTestAbstract {

    public BidirectionalRelationshipContractTestAll() {
        super(Constants.packagePrefix, 
                ImmutableMap.<Class<?>,Instantiator>of(
                    Agreement.class, new InstantiatorSimple(AgreementForTesting.class),
                    FixedAsset.class, new InstantiatorSimple(FixedAssetForTesting.class),
                    Party.class, new InstantiatorSimple(PartyForTesting.class),
                    Geography.class, new InstantiatorSimple(GeographyForTesting.class)
                ));
        withLoggingTo(System.out);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Instantiator doInstantiatorFor(Class<?> cls) {
        if(WithStartDate.class.isAssignableFrom(cls)) {
            return new InstantiatorForWithStartDate((Class<? extends WithStartDate>) cls);
        }
        if(ComparableByCode.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByCode((Class<? extends ComparableByCode<?>>) cls);
        }
        if(ComparableByName.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByName((Class<? extends ComparableByName<?>>) cls);
        }
        if(ComparableByDescription.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByDescription((Class<? extends ComparableByDescription<?>>) cls);
        }
        if(ComparableByReference.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByReference((Class<? extends ComparableByReference<?>>) cls);
        }
        return super.doInstantiatorFor(cls);
    }
}
