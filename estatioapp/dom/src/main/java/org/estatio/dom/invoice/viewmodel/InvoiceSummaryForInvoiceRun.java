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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.invoice.Invoice;

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
                                "  INNER JOIN \"FixedAsset\"  " +
                                "    ON \"FixedAsset\".\"id\"  = \"Invoice\".\"fixedAssetId\" " +
                                "  INNER JOIN \"InvoiceItem\" " +
                                "    ON \"InvoiceItem\".\"invoiceId\" = \"Invoice\".\"id\" " +
                                "WHERE " +
                                "   NOT \"Invoice\".\"runId\" IS NULL " +
                                "GROUP BY " +
                                "   \"Invoice\".\"runId\"")
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByRunId", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.viewmodel.InvoiceSummaryForInvoiceRun " +
                        "WHERE " +
                        "runId == :runId ")
})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@ViewModel
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class InvoiceSummaryForInvoiceRun extends InvoiceSummaryAbstract {

    public String iconName() {
        return "InvoiceSummary";
    }

    public String title() {
        return "Invoice run ".concat(getRunId().substring(0, 21));
    }

    // //////////////////////////////////////

    private String runId;

    @PropertyLayout(typicalLength = 100)
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

    @Property(hidden = Where.EVERYWHERE)
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

    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Invoice> getInvoices() {
        List<Invoice> findInvoicesByRunId = invoicesService.findInvoicesByRunId(runId);
        return findInvoicesByRunId;
    }

}
