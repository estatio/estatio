/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.application.dom;

import com.google.common.collect.ImmutableMap;

import org.apache.isis.core.unittestsupport.bidir.BidirectionalRelationshipContractTestAbstract;
import org.apache.isis.core.unittestsupport.bidir.Instantiator;
import org.apache.isis.core.unittestsupport.bidir.InstantiatorSimple;

import org.incode.module.base.dom.with.WithCodeComparable;
import org.incode.module.base.dom.with.WithDescriptionComparable;
import org.incode.module.base.dom.with.WithNameComparable;
import org.incode.module.base.dom.with.WithReferenceComparable;
import org.incode.module.base.dom.with.WithStartDate;
import org.incode.module.unittestsupport.dom.with.InstantiatorForComparableByCode;
import org.incode.module.unittestsupport.dom.with.InstantiatorForComparableByDescription;
import org.incode.module.unittestsupport.dom.with.InstantiatorForComparableByName;
import org.incode.module.unittestsupport.dom.with.InstantiatorForComparableByReference;
import org.incode.module.unittestsupport.dom.with.InstantiatorForWithStartDate;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementForTesting;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetForTesting;
import org.estatio.module.lease.dom.InstantiatorForLeaseItem;
import org.estatio.module.lease.dom.InstantiatorForLeaseTerm;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

public class BidirectionalRelationshipContractForEstatio_Test extends BidirectionalRelationshipContractTestAbstract {

    public BidirectionalRelationshipContractForEstatio_Test() {
        super("org.estatio",
                ImmutableMap.<Class<?>,Instantiator>builder()
                    .put(Agreement.class, new InstantiatorSimple(AgreementForTesting.class))
                    .put(FixedAsset.class, new InstantiatorSimple(FixedAssetForTesting.class))
                    .put(Party.class, new InstantiatorSimple(PartyForTesting.class))
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
