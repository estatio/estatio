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
package org.estatio.dom.base;

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

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class BidirectionalRelationshipContractForEstatioTest extends BidirectionalRelationshipContractTestAbstract {

    public BidirectionalRelationshipContractForEstatioTest() {
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
