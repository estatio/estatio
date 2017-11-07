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

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "InvoiceSummaryForPropertyInvoiceDate",
        schema = "dbo",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"dbo\".\"InvoiceSummaryForPropertyInvoiceDate\" " +
                                "( " +
                                "  {this.atPath}, " +
                                "  {this.sellerReference}, " +
                                "  {this.invoiceDate}, " +
                                "  {this.total}, " +
                                "  {this.netAmount}, " +
                                "  {this.vatAmount}, " +
                                "  {this.grossAmount} " +
                                ") AS " +
                                "SELECT " +
                                "  i.\"atPath\", " +
                                "  p.\"reference\" , " +
                                "  i.\"invoiceDate\", " +
                                "  COUNT(DISTINCT(i.\"id\")) AS \"total\", " +
                                "   SUM(ii.\"netAmount\") AS \"netAmount\", " +
                                "   SUM(ii.\"vatAmount\") AS \"vatAmount\", " +
                                "   SUM(ii.\"grossAmount\") AS \"grossAmount\" " +
                                "FROM \"dbo\".\"Invoice\" i " +
                                "  INNER JOIN \"dbo\".\"InvoiceItem\" ii " +
                                "    ON ii.\"invoiceId\" = i.\"id\" " +
                                "  INNER JOIN \"dbo\".\"Party\" p " +
                                "    ON p.\"id\" = i.\"sellerPartyId\" " +
                                "WHERE i.\"discriminator\" = 'org.estatio.dom.invoice.Invoice' " +
                                "GROUP BY " +
                                "  i.\"atPath\", " +
                                "  p.\"reference\", " +
                                "  i.\"invoiceDate\"")
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "all", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyInvoiceDate " +
                        "ORDER BY invoiceDate DESCENDING, atPath "),
        @javax.jdo.annotations.Query(
                name = "byInvoiceDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyInvoiceDate " +
                        "WHERE invoiceDate >= :date " +
                        "ORDER BY invoiceDate DESCENDING, atPath ")
})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@ViewModel
@DomainObject(editing = Editing.DISABLED)
public class InvoiceSummaryForPropertyInvoiceDate extends InvoiceSummaryAbstract {

    public String iconName() {
        return "InvoiceSummary";
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getAtPath())
                .withName(getSellerReference())
                .withName(getInvoiceDate())
                .toString();
    }

    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String atPath;

    public ApplicationTenancy getApplicationTenancy(){
        return applicationTenancyRepository.findByPath(getAtPath());
    }

    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    private String sellerReference;

    /**
     * Annotated as {@link javax.jdo.annotations.NotPersistent not persistent}
     * because not mapped in the <tt>view-definition</tt>.
     */
    @javax.jdo.annotations.NotPersistent
    private Party seller;

    public Party getSeller() {
        if (seller == null) {
            seller = partyRepository.findPartyByReference(getSellerReference());
        }
        return seller;
    }

    @Getter @Setter
    private LocalDate invoiceDate;

    @Getter @Setter
    private int total;

    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    private BigDecimal vatAmount;

    @Getter @Setter
    private BigDecimal netAmount;

    @Getter @Setter
    private BigDecimal grossAmount;

    // //////////////////////////////////////

    @CollectionLayout(defaultView = "table")
    public List<InvoiceForLease> getInvoices() {
        return invoiceForLeaseRepository
                .findByApplicationTenancyPathAndSellerAndInvoiceDate(getAtPath(), getSeller(), getInvoiceDate());
    }

    // //////////////////////////////////////


    @Inject
    PartyRepository partyRepository;

}
