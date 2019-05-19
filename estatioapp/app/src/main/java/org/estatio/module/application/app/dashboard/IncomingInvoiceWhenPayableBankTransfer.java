package org.estatio.module.application.app.dashboard;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.LineCache;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static org.apache.isis.applib.annotation.Projecting.PROJECTED;

@XmlRootElement(name = "homePage.IncomingInvoiceWhenPayableBankTransfer")
@XmlType(
    propOrder = {
            "incomingInvoice",
            "lastApprovedBy",
            "lastApprovedOn",
            "codaDocHead",
            "userStatus",
    }
)
@DomainObject(objectType = "homePage.IncomingInvoiceWhenPayableBankTransfer")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class IncomingInvoiceWhenPayableBankTransfer {

    public IncomingInvoiceWhenPayableBankTransfer(final IncomingInvoice incomingInvoice, final CodaDocHead codaDocHead) {
        final List<IncomingInvoice.ApprovalString> approvals = incomingInvoice.getApprovals();
        Collections.reverse(approvals);
        if(!approvals.isEmpty()) {
            final IncomingInvoice.ApprovalString approval = approvals.get(0);
            this.lastApprovedOn = approval.getCompletedOnAsDate();
            this.lastApprovedBy = approval.getCompletedBy();
        }
        this.codaDocHead = codaDocHead;
        this.incomingInvoice = incomingInvoice;

        this.userStatus = codaDocHead.getSummaryLineUserStatus(LineCache.DEFAULT);
    }

    @org.apache.isis.applib.annotation.Property(projecting = PROJECTED)
    @org.apache.isis.applib.annotation.PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    @XmlTransient
    @org.apache.isis.applib.annotation.PropertyLayout(named = "Supplier")
    public Party getSeller() {
        return getIncomingInvoice().getSeller();
    }

    @XmlTransient
    public Property getProperty() {
        return getIncomingInvoice().getProperty();
    }

    @XmlTransient
    public BigDecimal getGrossAmount() {
        return getIncomingInvoice().getGrossAmount();
    }

    @XmlTransient
    public LocalDate getDateReceived() {
        return getIncomingInvoice().getDateReceived();
    }

    @Getter @Setter @Nullable
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate lastApprovedOn;

    @Getter @Setter @Nullable
    private String lastApprovedBy;

    @Getter @Setter @Nullable
    private Character userStatus;

    @Getter @Setter @Nullable
    private CodaDocHead codaDocHead;

}
