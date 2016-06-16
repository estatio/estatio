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
package org.estatio.dom.lease;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.FatalException;

import org.estatio.dom.PowerType;
import org.estatio.dom.utils.StringUtils;

public enum LeaseItemType implements PowerType<LeaseTerm> {

    RENT(LeaseTermForIndexable.class, true, false, true),
    RENT_FIXED(LeaseTermForFixed.class, false, false, true),
    TURNOVER_RENT(LeaseTermForTurnoverRent.class, true, true, true),
    RENTAL_FEE(LeaseTermForPercentage.class, true, true, true),
    DEPOSIT(LeaseTermForDeposit.class, false, true, true),
    SERVICE_CHARGE(LeaseTermForServiceCharge.class, true, false, true),
    SERVICE_CHARGE_BUDGETED(LeaseTermForServiceCharge.class, false, false, false),
    SERVICE_CHARGE_INDEXABLE(LeaseTermForIndexable.class, true, false, true),
    DISCOUNT(LeaseTermForFixed.class, false, false, true),
    RENT_DISCOUNT(LeaseTermForIndexable.class, false, false, true),
    ENTRY_FEE(LeaseTermForFixed.class, false, false, true),
    TAX(LeaseTermForTax.class, true, true, false);

    private final Class<? extends LeaseTerm> clss;
    private final boolean autoCreateTerms;
    private final boolean useSource;
    private final boolean allowOpenEndDate;


    // //////////////////////////////////////

    private LeaseItemType(
            final Class<? extends LeaseTerm> clss,
            final boolean autoCreateTerms,
            final boolean useSource,
            final boolean allowOpenEndDate) {
        this.clss = clss;
        this.autoCreateTerms = autoCreateTerms;
        this.useSource = useSource;
        this.allowOpenEndDate = allowOpenEndDate;
    }

    // //////////////////////////////////////

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    // //////////////////////////////////////

    public LeaseTerm create(final DomainObjectContainer container) {
        try {
            LeaseTerm term = container.newTransientInstance(clss);
            return term;
        } catch (Exception ex) {
            throw new FatalException(ex);
        }
    }

    // //////////////////////////////////////

    public boolean autoCreateTerms() {
        return autoCreateTerms;
    }

    public boolean useSource() {
        return useSource; }

    public boolean allowOpenEndDate() {
        return allowOpenEndDate;
    }
}
