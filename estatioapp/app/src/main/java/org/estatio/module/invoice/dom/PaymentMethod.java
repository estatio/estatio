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
package org.estatio.module.invoice.dom;

import org.incode.module.base.dom.utils.StringUtils;

public enum PaymentMethod {

    DIRECT_DEBIT,
    BILLING_ACCOUNT,
    BANK_TRANSFER,
    CASH,
    CHEQUE,
    CREDIT_CARD,
    REFUND_BY_SUPPLIER,
    MANUAL_PROCESS;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
    
    public boolean isDirectDebit() {
        return this == DIRECT_DEBIT;
    }

    public static class Meta {

        public final static int MAX_LEN = 30;

        private Meta() {}

    }

    public boolean requiresNoApprovalInItaly() {
        return this == DIRECT_DEBIT || this == MANUAL_PROCESS;
    }

}
