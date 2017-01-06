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
package org.estatio.dom.leaseinvoicing.viewmodel;

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

import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.leaseinvoicing.InvoiceForLease;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "InvoiceSummaryForPropertyDueDateStatus",
        schema = "dbo",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"dbo\".\"InvoiceSummaryForPropertyDueDateStatus\" " +
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
                                "  i.\"atPath\", " +
                                "  p.\"reference\" , " +
                                "  i.\"dueDate\", " +
                                "  i.\"status\", " +
                                "  COUNT(DISTINCT(i.\"id\")) AS \"total\", " +
                                "   SUM(ii.\"netAmount\") AS \"netAmount\", " +
                                "   SUM(ii.\"vatAmount\") AS \"vatAmount\", " +
                                "   SUM(ii.\"grossAmount\") AS \"grossAmount\" " +
                                "FROM \"dbo\".\"Invoice\" i " +
                                "  INNER JOIN \"dbo\".\"InvoiceItem\" ii " +
                                "    ON ii.\"invoiceId\" = i.\"id\" " +
                                "  INNER JOIN \"dbo\".\"Party\" p " +
                                "    ON p.\"id\" = i.\"sellerPartyId\" " +
                                "GROUP BY " +
                                "  i.\"atPath\", " +
                                "  p.\"reference\", " +
                                "  i.\"dueDate\", " +
                                "  i.\"status\"")
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
@DomainObject(editing = Editing.DISABLED)
public class InvoiceSummaryForPropertyDueDateStatus extends InvoiceSummaryAbstract {

    public String iconName() {
        return "InvoiceSummary";
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getAtPath())
                .withName(getSellerReference())
                .withName(getDueDate())
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
    private InvoiceStatus status;

    @Getter @Setter
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

    @CollectionLayout(defaultView = "table")
    public List<InvoiceForLease> getInvoices() {
        return invoiceForLeaseRepository
                .findByApplicationTenancyPathAndSellerAndDueDateAndStatus(getAtPath(), getSeller(), getDueDate(), getStatus());
    }

    // //////////////////////////////////////

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PartyRepository partyRepository;

}
