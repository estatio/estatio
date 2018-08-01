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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.spi.DocumentAttachmentAdvisor;

import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.comms.InvoiceForLease_attachSupportingDocument;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

// NOTE: this view also is maintained by fly db
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
                                "WHERE i.\"discriminator\" = 'org.estatio.dom.invoice.Invoice' " +
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
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus " +
                        "WHERE status == :status "),
        @javax.jdo.annotations.Query(
                name = "findByStatusAndDueDateAfter", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus " +
                        "WHERE status == :status " +
                        "   && dueDate >= :dueDateAfter "
        ),
        @javax.jdo.annotations.Query(
                name = "findByAtPathAndSellerReferenceAndStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus " +
                        "WHERE atPath == :atPath " +
                        "   && sellerReference == :sellerReference " +
                        "   && status == :status "
        ),
        @javax.jdo.annotations.Query(
                name = "findByAtPathAndSellerReferenceAndStatusAndDueDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus " +
                        "WHERE atPath == :atPath " +
                        "   && sellerReference == :sellerReference " +
                        "   && status == :status " +
                        "   && dueDate == :dueDate "
        )
})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@ViewModel
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
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

    public ApplicationTenancy getApplicationTenancy() {
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

    @CollectionLayout(defaultView = "table")
    public List<InvoiceForLease> getInvoices() {
        return invoiceForLeaseRepository
                .findByApplicationTenancyPathAndSellerAndDueDateAndStatus(getAtPath(), getSeller(), getDueDate(), getStatus());
    }

    public List<InvoiceSummaryForPropertyDueDateStatus> changeDueDates(final LocalDate newDueDate) {
        getInvoices().forEach(invoice -> wrapperFactory.wrap(invoice).changeDueDate(newDueDate));
        return invoiceSummaryForPropertyDueDateStatusRepository.findByAtPathAndSellerReferenceAndStatusAndDueDate(getAtPath(), getSellerReference(), getStatus(), newDueDate);
    }

    public String disableChangeDueDates() {
        return !getStatus().invoiceIsChangable() ? "Only new and approved invoices can be changed" : null;
    }

    @Action(associateWith = "invoices", associateWithSequence = "1" )
    public InvoiceSummaryForPropertyDueDateStatus attachSupportingDocument(
            final List<InvoiceForLease> invoices,
            final DocumentType supportingDocumentType,
            @Parameter(fileAccept = "application/pdf")
            @ParameterLayout(named = "Receipt (PDF)")
            final Blob blob,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String fileName,
            final String roleName
    ){
        invoices.forEach(invoice->{
            final InvoiceForLease_attachSupportingDocument invoice_attachSupportingDocument = factoryService.mixin(InvoiceForLease_attachSupportingDocument.class, invoice);
                    try {
                        wrapperFactory.wrap(invoice_attachSupportingDocument).$$(
                            supportingDocumentType, blob, fileName, roleName
                        );
                    } catch (IOException e) {
                        // do nothing
                    }
                }
        );
        return this;
    }

    public List<DocumentType> choices1AttachSupportingDocument() {
        return documentAttachmentAdvisor.documentTypeChoicesFor(null);
    }

    public DocumentType default1AttachSupportingDocument() {
        return documentAttachmentAdvisor.documentTypeDefaultFor(null);
    }

    public List<String> choices4AttachSupportingDocument() {
        return documentAttachmentAdvisor.roleNameChoicesFor(null);
    }

    public String default4AttachSupportingDocument() {
        return documentAttachmentAdvisor.roleNameDefaultFor(null);
    }

    @Inject
    PartyRepository partyRepository;

    @Inject
    InvoiceSummaryForPropertyDueDateStatusRepository invoiceSummaryForPropertyDueDateStatusRepository;

    @Inject
    FactoryService factoryService;

    @Inject
    DocumentAttachmentAdvisor documentAttachmentAdvisor;


}
