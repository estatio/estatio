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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.invoice.Invoice;

import lombok.Getter;
import lombok.Setter;

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
                                "  {this.atPath}, " +
                                "  {this.runId}, " +
                                "  {this.total}, " +
                                "  {this.netAmount}, " +
                                "  {this.vatAmount}, " +
                                "  {this.grossAmount} " +
                                ") AS " +
                                "SELECT " +
                                "   \"Invoice\".\"atPath\" , " +
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
                                "   \"Invoice\".\"runId\", \"Invoice\".\"atPath\"")
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
@DomainObject(editing = Editing.DISABLED, nature = Nature.VIEW_MODEL)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class InvoiceSummaryForInvoiceRun extends InvoiceSummaryAbstract {

    public String iconName() {
        return "InvoiceSummary";
    }

    public String title() {
        return "Invoice run ".concat(getRunId().substring(0, 21));
    }

    // //////////////////////////////////////

    private String atPath;

    @Property(hidden = Where.EVERYWHERE)
    public String getAtPath() {
        return atPath;
    }

    public void setAtPath(final String atPath) {
        this.atPath = atPath;
    }

    public ApplicationTenancy getApplicationTenancy(){
        return applicationTenancyRepository.findByPath(getAtPath());
    }

    @PropertyLayout(typicalLength = 100)
    @javax.jdo.annotations.Column(allowsNull = "false")
    @Getter @Setter
    private String runId;

    // //////////////////////////////////////

    @Getter @Setter
    private int total;

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private BigDecimal vatAmount;

    // //////////////////////////////////////

    @Getter @Setter
    private BigDecimal netAmount;

    // //////////////////////////////////////

    @Getter @Setter
    private BigDecimal grossAmount;

    // //////////////////////////////////////

    @Override public Object invoiceAll(final LocalDate invoiceDate) {
        return super.invoiceAll(invoiceDate);
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Invoice> getInvoices() {
        return invoiceRepository.findByRunIdAndApplicationTenancyPath(runId, getAtPath());
    }

}
