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
package org.estatio.module.lease.dom.invoicing.summary;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
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
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

import lombok.Getter;
import lombok.Setter;

// NOTE: this view also is maintained by fly db
/**
 * View model that surfaces information about each runId along with summary
 * details of its invoices in their various states.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "InvoiceSummaryForInvoiceRun",
        schema = "dbo",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"dbo\".\"InvoiceSummaryForInvoiceRun\" " +
                                "( " +
                                "  {this.atPath}, " +
                                "  {this.runId}, " +
                                "  {this.total}, " +
                                "  {this.netAmount}, " +
                                "  {this.vatAmount}, " +
                                "  {this.grossAmount} " +
                                ") AS " +
                                "SELECT " +
                                "   i.\"atPath\" , " +
                                "   i.\"runId\" , " +
                                "   COUNT(DISTINCT(i.\"id\")) AS \"total\", " +
                                "   SUM(ii.\"netAmount\") AS \"netAmount\", " +
                                "   SUM(ii.\"vatAmount\") AS \"vatAmount\", " +
                                "   SUM(ii.\"grossAmount\") AS \"grossAmount\" " +
                                "FROM \"dbo\".\"Invoice\" i " +
                                "  INNER JOIN \"dbo\".\"Lease\" l  " +
                                "    ON i.\"leaseId\" = l.\"id\" " +
                                "  INNER JOIN \"dbo\".\"FixedAsset\" fa " +
                                "    ON fa.\"id\"  = i.\"fixedAssetId\" " +
                                "  INNER JOIN \"dbo\".\"InvoiceItem\" ii " +
                                "    ON ii.\"invoiceId\" = i.\"id\" " +
                                "WHERE " +
                                "   NOT i.\"runId\" IS NULL " +
                                "   AND i.\"discriminator\" = 'org.estatio.dom.invoice.Invoice' " +
                                "GROUP BY " +
                                "   i.\"runId\", i.\"atPath\"")
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByRunId", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForInvoiceRun " +
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

    @Getter @Setter
    private String atPath;

    @Programmatic
    public ApplicationTenancy getApplicationTenancy() {
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

    public InvoiceSummaryForInvoiceRun changeDueDates(final LocalDate newDueDate) {
        getInvoices().forEach(invoice -> wrapperFactory.wrap(invoice).changeDueDate(newDueDate));
        return invoiceSummaryForInvoiceRunRepository.findByRunId(this.runId);
    }

    // //////////////////////////////////////

    @CollectionLayout(defaultView = "table")
    public List<InvoiceForLease> getInvoices() {
        return invoiceForLeaseRepository.findByRunIdAndApplicationTenancyPath(runId, getAtPath());
    }

    // //////////////////////////////////////

    @Inject
    protected InvoiceSummaryForInvoiceRunRepository invoiceSummaryForInvoiceRunRepository;
}
