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
package org.estatio.dom.lease.invoicing;

import org.junit.Test;

import org.estatio.dom.lease.Lease;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceServiceMenuAndContributionsTest {

    @Test
    public void test() {
        // given
        InvoiceServiceMenuAndContributions invoiceService = new InvoiceServiceMenuAndContributions();
        Lease lease = new Lease();

        // when
        assertThat(lease.getProperty()).isNull();

        // then
        assertThat(invoiceService.disableCalculate(lease, null, null, null, null, null)).isEqualTo("Please set occupancy first");

    }

}
