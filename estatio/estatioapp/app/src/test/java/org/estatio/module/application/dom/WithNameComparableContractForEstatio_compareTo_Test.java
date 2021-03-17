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

import org.incode.module.base.dom.with.WithNameComparable;
import org.incode.module.unittestsupport.dom.with.ComparableByNameContractTestAbstract_compareTo;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetForTesting;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

/**
 * Automatically tests all domain objects implementing
 * {@link WithNameComparable}.
 */
public class WithNameComparableContractForEstatio_compareTo_Test extends ComparableByNameContractTestAbstract_compareTo {

    public WithNameComparableContractForEstatio_compareTo_Test() {
        super("org.estatio", noninstantiableSubstitutes());
    }

    static ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes() {
        return ImmutableMap.<Class<?>, Class<?>> of(
                FixedAsset.class, FixedAssetForTesting.class,
                Party.class, PartyForTesting.class);
    }

}
