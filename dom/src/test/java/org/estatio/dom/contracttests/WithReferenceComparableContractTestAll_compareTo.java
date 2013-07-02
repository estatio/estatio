/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.ComparableByReferenceContractTestAbstract_compareTo;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.geography.Geography;
import org.estatio.dom.geography.GeographyForTesting;


/**
 * Automatically tests all domain objects implementing {@link WithReferenceComparable}.
 */
public class WithReferenceComparableContractTestAll_compareTo extends ComparableByReferenceContractTestAbstract_compareTo {

    public WithReferenceComparableContractTestAll_compareTo() {
        super(Constants.packagePrefix, noninstantiableSubstitutes());
    }

    static ImmutableMap<Class<?>, Class<?>> noninstantiableSubstitutes() {
        return ImmutableMap.<Class<?>,Class<?>>of(
                Agreement.class, AgreementForTesting.class,
                Geography.class, GeographyForTesting.class);
    }

}
