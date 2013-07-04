/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.invoice;

import java.util.List;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class InvoiceTest_compareTo extends ComparableContractTest_compareTo<Invoice> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<Invoice>> orderedTuples() {
        return listOf(
                listOf(
                        newInvoice(null),
                        newInvoice("0000123"),
                        newInvoice("0000123"),
                        newInvoice("0000124")
                        )
                );
    }

    private Invoice newInvoice(String number) {
        final Invoice inv = new Invoice();
        inv.setInvoiceNumber(number);
        return inv;
    }

}
