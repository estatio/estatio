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
package org.estatio.dom.tax;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class TaxRateTest_compareTo extends ComparableContractTest_compareTo<TaxRate> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<TaxRate>> orderedTuples() {
        return listOf(
                listOf(
                        newTaxRate(null),
                        newTaxRate(new LocalDate(2012,4,2)),
                        newTaxRate(new LocalDate(2012,4,2)),
                        newTaxRate(new LocalDate(2012,3,1))
                        )
                );
    }

    private TaxRate newTaxRate(
            LocalDate startDate) {
        final TaxRate tr = new TaxRate();
        tr.setStartDate(startDate);
        return tr;
    }

}
