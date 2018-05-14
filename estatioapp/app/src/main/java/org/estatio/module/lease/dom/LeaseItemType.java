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
package org.estatio.module.lease.dom;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;

import org.apache.isis.applib.FatalException;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.base.dom.PowerType;
import org.incode.module.base.dom.utils.StringUtils;

public enum LeaseItemType implements PowerType<LeaseTerm> {

    RENT(LeaseTermForIndexable.class, true, false, true),
    RENT_FIXED(LeaseTermForFixed.class, false, false, true),
    RENT_DISCOUNT(LeaseTermForIndexable.class, false, false, true),
    RENT_DISCOUNT_FIXED(LeaseTermForFixed.class, false, false, true),
    TURNOVER_RENT(LeaseTermForTurnoverRent.class, true, true, true),
    TURNOVER_RENT_FIXED(LeaseTermForFixed.class, true, false, false),
    DEPOSIT(LeaseTermForDeposit.class, false, true, true),
    SERVICE_CHARGE(LeaseTermForServiceCharge.class, true, false, true),
    SERVICE_CHARGE_BUDGETED(LeaseTermForServiceCharge.class, false, false, false),
    SERVICE_CHARGE_INDEXABLE(LeaseTermForIndexable.class, true, false, true),
    SERVICE_CHARGE_DISCOUNT_FIXED(LeaseTermForFixed.class, false, false, true),
    ENTRY_FEE(LeaseTermForFixed.class, false, false, true),
    TAX(LeaseTermForTax.class, true, true, false),
    MARKETING(LeaseTermForServiceCharge.class, true, false, true),
    PROPERTY_TAX(LeaseTermForServiceCharge.class, true, false, true);

    private final Class<? extends LeaseTerm> cls;
    private final boolean autoCreateTerms;
    private final boolean useSource;
    private final boolean allowOpenEndDate;

    // //////////////////////////////////////

    private LeaseItemType(
            final Class<? extends LeaseTerm> cls,
            final boolean autoCreateTerms,
            final boolean useSource,
            final boolean allowOpenEndDate) {
        this.cls = cls;
        this.autoCreateTerms = autoCreateTerms;
        this.useSource = useSource;
        this.allowOpenEndDate = allowOpenEndDate;
    }

    // //////////////////////////////////////

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public boolean isCreate(Class<?> cls) {
        return cls.isAssignableFrom(this.cls);
    }

    // //////////////////////////////////////

    public LeaseTerm create(final FactoryService factoryService) {
        try {
            LeaseTerm term = factoryService.instantiate(cls);
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

    // //////////////////////////////////////

    public static class Meta {
        private Meta(){}

        public final static int MAX_LEN = 30;
    }

    public static List<LeaseItemType> typesForLeaseTermForServiceCharge(){
        return EnumUtils.getEnumList(LeaseItemType.class).stream().filter(x->x.cls == LeaseTermForServiceCharge.class).collect(Collectors.toList());
    }

}
