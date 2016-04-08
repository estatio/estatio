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
package org.estatio.dom.invoice;

import org.estatio.dom.utils.StringUtils;

public enum PaymentMethod {

    DIRECT_DEBIT(org.estatio.canonical.invoice.v1.PaymentMethod.DIRECT_DEBIT),
    BILLING_ACCOUNT(null),
    BANK_TRANSFER(org.estatio.canonical.invoice.v1.PaymentMethod.BANK_TRANSFER),
    CASH(org.estatio.canonical.invoice.v1.PaymentMethod.CASH),
    CHEQUE(org.estatio.canonical.invoice.v1.PaymentMethod.CHEQUE);

    private final org.estatio.canonical.invoice.v1.PaymentMethod canonicalPaymentMethod;

    PaymentMethod(final org.estatio.canonical.invoice.v1.PaymentMethod canonicalPaymentMethod) {

        this.canonicalPaymentMethod = canonicalPaymentMethod;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
    
    public boolean isDirectDebit() {
        return this == DIRECT_DEBIT;
    }

    public org.estatio.canonical.invoice.v1.PaymentMethod asDto() {
        if(canonicalPaymentMethod == null) {
            throw new IllegalArgumentException(String.format("Payment method '%s' could not be mapped", name()));
        }
        return canonicalPaymentMethod;
    }

}
