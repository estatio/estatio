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
package org.estatio.dom.lease.invoicing.ssrs;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.apptenancy.WithApplicationTenancy;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.lease.invoicing.ssrs.InvoiceItemAttributesVM"
)
public class InvoiceItemAttributesVM implements WithApplicationTenancy {

    public InvoiceItemAttributesVM() {
    }

    public InvoiceItemAttributesVM(InvoiceItemForLease item) {
        invoiceItem = item;
    }

    @Getter @Setter
    private InvoiceItemForLease invoiceItem;

    @Programmatic
    public Charge getCharge() {
        return invoiceItem.getCharge();
    }

    @Programmatic
    public LocalDate getEffectiveEndDate() {
        return invoiceItem.getEffectiveEndDate();
    }

    @Programmatic
    public LocalDate getEffectiveStartDate() {
        return invoiceItem.getEffectiveStartDate();
    }

    @Programmatic
    public LocalDate getStartDate() {
        return invoiceItem.getStartDate();
    }

    @Programmatic
    public Boolean getAdjustment() {
        return invoiceItem.getAdjustment();
    }

    @Programmatic
    public LocalDate getDueDate() {
        return invoiceItem.getDueDate();
    }

    @Programmatic
    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return invoiceItem.getApplicationTenancy();
    }

    @Programmatic
    @Override
    public String getAtPath() {
        return invoiceItem.getAtPath();
    }

}
