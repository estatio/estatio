package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.apache.isis.core.unittestsupport.bidir.BidirectionalRelationshipContractTestAbstract;
import org.apache.isis.core.unittestsupport.bidir.Instantiator;
import org.apache.isis.core.unittestsupport.bidir.InstantiatorSimple;

import org.estatio.dom.WithCodeComparable;
import org.estatio.dom.WithDescriptionComparable;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.InstantiatorForComparableByCode;
import org.estatio.dom.InstantiatorForComparableByDescription;
import org.estatio.dom.InstantiatorForComparableByName;
import org.estatio.dom.InstantiatorForComparableByReference;
import org.estatio.dom.InstantiatorForWithStartDate;
import org.estatio.dom.WithStartDate;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.geography.Geography;
import org.estatio.dom.geography.GeographyForTesting;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class BidirectionalRelationshipContractTestAll extends BidirectionalRelationshipContractTestAbstract {

    public BidirectionalRelationshipContractTestAll() {
        super(Constants.packagePrefix, 
                ImmutableMap.<Class<?>,Instantiator>builder()
                    .put(Agreement.class, new InstantiatorSimple(AgreementForTesting.class))
                    .put(FixedAsset.class, new InstantiatorSimple(FixedAssetForTesting.class))
                    .put(Party.class, new InstantiatorSimple(PartyForTesting.class))
                    .put(Geography.class, new InstantiatorSimple(GeographyForTesting.class))
                    .put(LeaseItem.class, new InstantiatorForLeaseItem())
                    .put(LeaseTerm.class, new InstantiatorForLeaseTerm())
                    .build()
                );
        withLoggingTo(System.out);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Instantiator doInstantiatorFor(Class<?> cls) {
        if(WithStartDate.class.isAssignableFrom(cls)) {
            return new InstantiatorForWithStartDate((Class<? extends WithStartDate>) cls);
        }
        if(WithCodeComparable.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByCode((Class<? extends WithCodeComparable<?>>) cls);
        }
        if(WithNameComparable.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByName((Class<? extends WithNameComparable<?>>) cls);
        }
        if(WithDescriptionComparable.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByDescription((Class<? extends WithDescriptionComparable<?>>) cls);
        }
        if(WithReferenceComparable.class.isAssignableFrom(cls)) {
            return new InstantiatorForComparableByReference((Class<? extends WithReferenceComparable<?>>) cls);
        }
        return super.doInstantiatorFor(cls);
    }
}
