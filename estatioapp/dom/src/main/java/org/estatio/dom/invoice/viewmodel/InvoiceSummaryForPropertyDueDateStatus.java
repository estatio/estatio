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

import javax.inject.Inject;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "InvoiceSummaryForPropertyDueDateStatus",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"InvoiceSummaryForPropertyDueDateStatus\" " +
                                "( " +
                                "  {this.atPath}, " +
                                "  {this.sellerReference}, " +
                                "  {this.dueDate}, " +
                                "  {this.status}, " +
                                "  {this.total}, " +
                                "  {this.netAmount}, " +
                                "  {this.vatAmount}, " +
                                "  {this.grossAmount} " +
                                ") AS " +
                                "SELECT " +
                                "  \"Invoice\".\"atPath\", " +
                                "  \"Party\".\"reference\" , " +
                                "  \"Invoice\".\"dueDate\", " +
                                "  \"Invoice\".\"status\", " +
                                "  COUNT(DISTINCT(\"Invoice\".\"id\")) AS \"total\", " +
                                "   SUM(\"InvoiceItem\".\"netAmount\") AS \"netAmount\", " +
                                "   SUM(\"InvoiceItem\".\"vatAmount\") AS \"vatAmount\", " +
                                "   SUM(\"InvoiceItem\".\"grossAmount\") AS \"grossAmount\" " +
                                "FROM \"Invoice\" " +
                                "  INNER JOIN \"InvoiceItem\" " +
                                "    ON \"InvoiceItem\".\"invoiceId\" = \"Invoice\".\"id\" " +
                                "  INNER JOIN \"Party\" " +
                                "    ON \"Party\".\"id\" = \"Invoice\".\"sellerPartyId\" " +
                                "GROUP BY " +
                                "  \"Invoice\".\"atPath\", " +
                                "  \"Party\".\"reference\", " +
                                "  \"Invoice\".\"dueDate\", " +
                                "  \"Invoice\".\"status\"")
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus " +
                        "WHERE " +
                        "status == :status ")
})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@ViewModel
@Immutable
public class InvoiceSummaryForPropertyDueDateStatus extends InvoiceSummaryAbstract {

    public String iconName() {
        return "InvoiceSummary";
    }

    // //////////////////////////////////////

    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    @Title(sequence = "1")
    private String atPath;

    public ApplicationTenancy getApplicationTenancy(){
        return applicationTenancyRepository.findByPath(getAtPath());
    }

    @Getter @Setter
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    @Title(sequence = "2", prepend = " - ")
    private String sellerReference;

    /**
     * Annotated as {@link javax.jdo.annotations.NotPersistent not persistent}
     * because not mapped in the <tt>view-definition</tt>.
     */
    @javax.jdo.annotations.NotPersistent
    private Party seller;

    public Party getSeller() {
        if (seller == null) {
            seller = parties.findPartyByReference(getSellerReference());
        }
        return seller;
    }

    @Getter @Setter
    private InvoiceStatus status;

    @Getter @Setter
    @Title(sequence = "3", prepend = " - ")
    private LocalDate dueDate;

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

    @CollectionLayout(render = RenderType.EAGERLY)
    public List<Invoice> getInvoices() {
        return invoiceRepository.findByApplicationTenancyPathAndSellerAndDueDateAndStatus(getAtPath(), getSeller(), getDueDate(), getStatus());
    }

    // //////////////////////////////////////

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PropertyMenu propertyMenu;

    @Inject
    Parties parties;

}
