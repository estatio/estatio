/*
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
package org.estatio.dom.invoice.viewmodel;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.TypicalLength;
import org.apache.isis.applib.services.memento.MementoService.Memento;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoices;

/**
 * View model that surfaces information about each runId along with summary
 * details of its invoices in their various states.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "InvoiceSummaryForInvoiceRun",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"InvoiceSummaryForInvoiceRun\" " +
                                "( " +
                                "  {this.runId}, " +
                                "  {this.total}, " +
                                "  {this.netAmount}, " +
                                "  {this.vatAmount}, " +
                                "  {this.grossAmount} " +
                                ") AS " +
                                "SELECT " +
                                "   \"Invoice\".\"runId\" , " +
                                "   COUNT(DISTINCT(\"Invoice\".\"id\")) AS \"total\", " +
                                "   SUM(\"InvoiceItem\".\"netAmount\") AS \"netAmount\", " +
                                "   SUM(\"InvoiceItem\".\"vatAmount\") AS \"vatAmount\", " +
                                "   SUM(\"InvoiceItem\".\"grossAmount\") AS \"grossAmount\" " +
                                "FROM \"Invoice\" " +
                                "  INNER JOIN \"Lease\"   " +
                                "    ON \"Invoice\".\"leaseId\" = \"Lease\".\"id\" " +
                                "  INNER JOIN \"Occupancy\" " +
                                "    ON \"Lease\".\"id\" = \"Occupancy\".\"leaseId\" " +
                                "  INNER JOIN \"Unit\"   " +
                                "    ON \"Unit\".\"id\" = \"Occupancy\".\"unitId\" " +
                                "  INNER JOIN \"Property\" " +
                                "    ON \"Property\".\"id\" = \"Unit\".\"propertyId\" " +
                                "  INNER JOIN \"FixedAsset\" " +
                                "    ON \"FixedAsset\".\"id\" = \"Property\".\"id\" " +
                                "  INNER JOIN \"InvoiceItem\" " +
                                "    ON \"InvoiceItem\".\"invoiceId\" = \"Invoice\".\"id\" " +
                                "WHERE " +
                                "   NOT \"Invoice\".\"runId\" IS NULL " +
                                "GROUP BY " +
                                "   \"Invoice\".\"runId\"")
        })
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Bookmarkable
@Immutable
public class InvoiceSummaryForInvoiceRun extends EstatioViewModel {

    public Object approve() {
        for (Invoice invoice : getInvoices()) {
            invoice.approve();
        }
        return this;
    }

    public Object collect(
            final @Named("Are you sure?") Boolean confirm
            ) {
        for (Invoice invoice : getInvoices()) {
            invoice.doCollect();
        }
        return this;
    }

    public Object invoice(
            final @Named("Invoice Date") LocalDate invoiceDate,
            final @Named("Are you sure?") Boolean confirm
            ) {
        for (Invoice invoice : getInvoices()) {
            invoice.doInvoice(invoiceDate);
        }
        return this;
    }

    public LocalDate default0Invoice() {
        return getClockService().now();
    }

    public Object removeAll(final @Named("Confirm") Boolean confirm) {
        for (Invoice invoice : getInvoices()) {
            invoice.remove();
        }
        return this;
    }

    @Prototype
    public Object zapAll(final @Named("Confirm") Boolean confirm) {
        for (Invoice invoice : getInvoices()) {
            invoice.doRemove();
        }
        return this;
    }

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        final Memento memento = getMementoService().create();

        memento.set("runId", getRunId())
                .set("netAmount", getNetAmount())
                .set("vatAmount", getVatAmount())
                .set("grossAmount", getGrossAmount())
                .set("total", getTotal());

        return memento.asString();
    }

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public void viewModelInit(final String mementoStr) {
        final Memento memento = getMementoService().parse(mementoStr);

        setRunId(memento.get("runId", String.class));
        setNetAmount(memento.get("netAmount", BigDecimal.class));
        setVatAmount(memento.get("vatAmount", BigDecimal.class));
        setGrossAmount(memento.get("grossAmount", BigDecimal.class));
        setTotal(memento.get("total", Integer.class));
    }

    // //////////////////////////////////////

    private String runId;

    @TypicalLength(100)
    @javax.jdo.annotations.Column(allowsNull = "false")
    public String getRunId() {
        return runId;
    }

    public void setRunId(final String runId) {
        this.runId = runId;
    }

    // //////////////////////////////////////

    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    // //////////////////////////////////////

    private BigDecimal vatAmount;

    @Hidden
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(final BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    // //////////////////////////////////////

    private BigDecimal netAmount;

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    // //////////////////////////////////////

    private BigDecimal grossAmount;

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    // //////////////////////////////////////

    @Render(Type.EAGERLY)
    public List<Invoice> getInvoices() {
        List<Invoice> findInvoicesByRunId = invoicesService.findInvoicesByRunId(runId);
        return findInvoicesByRunId;
    }

    // //////////////////////////////////////

    private Invoices invoicesService;

    final public void injectInvoicesService(final Invoices invoicesService) {
        this.invoicesService = invoicesService;
    }

}
