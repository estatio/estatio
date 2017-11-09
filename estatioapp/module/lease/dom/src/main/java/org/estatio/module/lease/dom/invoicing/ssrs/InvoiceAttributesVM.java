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
package org.estatio.module.lease.dom.invoicing.ssrs;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;
import org.joda.time.Months;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.apptenancy.WithApplicationTenancy;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM"
)
public class InvoiceAttributesVM implements WithApplicationTenancy {

    public InvoiceAttributesVM() {
    }

    public InvoiceAttributesVM(final InvoiceForLease invoice) {
        this.invoice = invoice;
    }

    @Getter @Setter
    private InvoiceForLease invoice;

    @Programmatic
    public LeaseType getLeaseType() {
        return invoice.getLease().getLeaseType();
    }

    @Programmatic
    public String getLeasePropertyName() {
        Lease lease = invoice.getLease();
        if(lease == null) {
            return null;
        }
        Property property = lease.getProperty();
        if(property == null) {
            return null;
        }
        return property.getName();
    }

    @Programmatic
    public LocalDate getLeaseTenancyStartDate() {
        Lease lease = invoice.getLease();
        if(lease == null) {
            return null;
        }
        return lease.getTenancyStartDate();
    }

    @Programmatic
    public String getSellerName() {
        return invoice.getSeller().getName();
    }

    @Programmatic
    public LocalDate getDueDate() {
        return invoice.getDueDate();
    }

    @Programmatic
    public PaymentMethod getPaymentMethod() {
        return invoice.getPaymentMethod();
    }

    @Programmatic
    public String getSellerBankAccountBankName() {
        FinancialAccount financialAccount = invoice.getSellerBankAccount();
        if (!(financialAccount instanceof BankAccount)) {
            return null;
        }
        final BankAccount bankAccount = (BankAccount) financialAccount;
        Party bank = bankAccount.getBank();
        if(bank == null) {
            return null;
        }
        return bank.getName();
    }

    @Programmatic
    public String getSellerBankAccountIban() {
        FinancialAccount financialAccount = invoice.getSellerBankAccount();
        if (!(financialAccount instanceof BankAccount)) {
            return null;
        }
        final BankAccount bankAccount = (BankAccount) financialAccount;
        return bankAccount.getIban();
    }

    @Programmatic
    public String getCurrentOccupancyBrandName() {
        final Occupancy currentOccupancy = invoice.getCurrentOccupancy();
        if(currentOccupancy == null) {
            return null;
        }
        final Brand brand = currentOccupancy.getBrand();
        if(brand == null) {
            return null;
        }
        return brand.getName();
    }

    @Programmatic
    public String getCurrentOccupancyUnitReference() {
        final Unit unit = getCurrentOccupancyUnit();
        if(unit == null) {
            return null;
        }
        return unit.getReference();
    }

    @Programmatic
    public String getCurrentOccupancyUnitName() {
        final Unit unit = getCurrentOccupancyUnit();
        if(unit == null) {
            return null;
        }
        return unit.getName();
    }

    @Programmatic
    private Unit getCurrentOccupancyUnit() {
        final Occupancy currentOccupancy = invoice.getCurrentOccupancy();
        if(currentOccupancy == null) {
            return null;
        }
        return currentOccupancy.getUnit();
    }

    @Programmatic
    public boolean isCurrentOccupancyUnitReferenceNotV() {
        String ref = getCurrentOccupancyUnitReference();
        return ref != null && !ref.contains("-V");
    }

    @Programmatic
    public boolean isFixedAssetDefined() {
        return invoice.getFixedAsset() != null;
    }

    @Programmatic
    public String getChargeDescriptions() {
        final StringBuilder buf = new StringBuilder();
        SortedSet<InvoiceItem> invoiceItems = invoice.getItems();
        Set<String> descriptions =
                FluentIterable.from(invoiceItems)
                        .transform(x -> {
                            Charge charge = x.getCharge();
                            return charge != null ? charge.getDescription(): null;
                        })
                        .filter(Objects::nonNull)
                        .toSortedSet(Ordering.natural());
        final List<String> items = Lists.newArrayList(descriptions);
        final int numItems = items.size();
        for (int i = 0; i < numItems; i++) {
            buf.append(items.get(i));
            if(i == numItems - 2) {
                buf.append(" e ");
            } else
            if (i != numItems - 1){
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    @Programmatic
    public String getUnitName() {
        final Optional<InvoiceItem> invoiceItemOptional = invoice.getItems().stream().findFirst();
        if (invoiceItemOptional.isPresent()){
            final InvoiceItemForLease invoiceItem = (InvoiceItemForLease) invoiceItemOptional.get();
            final FixedAsset fixedAsset = invoiceItem.getFixedAsset();
            if (fixedAsset != null && fixedAsset instanceof Unit){
                return fixedAsset.getName();
            }
        }
        return null;
    }

    @Programmatic
    public BigDecimal getGrossAmount() {
        return invoice.getTotalGrossAmount();
    }

    @Programmatic
    public String getFrequency() {
        String string = getFrequencyElseNull();
        return string == null ? "OTHER" : string;
    }

    private String getFrequencyElseNull() {
        final SortedSet<InvoiceItem> items = invoice.getItems();
        if(items.isEmpty()) {
            return null;
        }
        final InvoiceItem item = items.first();
        final LocalDate startDate = item.getStartDate();
        final LocalDate endDate = item.getEndDate();
        if(startDate == null || endDate == null) {
            return null;
        }
        Months months = Months.monthsBetween(startDate, endDate.plusDays(1));
        switch (months.getMonths()) {
        case 12:
            return "YEAR";
        case 3:
            return "QUARTER";
        case 1:
            return "MONTH";
        }
        return null;
    }

    @Programmatic
    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return invoice.getApplicationTenancy();
    }

    @Programmatic
    @Override
    public String getAtPath() {
        return invoice.getAtPath();
    }

}