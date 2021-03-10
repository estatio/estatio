package org.estatio.module.capex.app.paydd;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_markAsPaidByDirectDebit;
import org.estatio.module.invoice.dom.PaymentMethod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import static org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository.*;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.paydd.manager.DirectDebitsManager"
)
@XmlRootElement(name = "DirectDebitManager")
@XmlType(
        propOrder = {
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectDebitsFraManager {

    public DirectDebitsFraManager() { }

    public String title() {
        return "Direct Debit Manager (France)";
    }


    @XmlTransient
    @Collection(notPersisted = true)
    public List<DirectDebitInvoiceViewModel> getIncomingInvoicesPayableByDirectDebit() {
        return queryResultsCache.execute(
                this::doGetDirectDebitInvoiceViewModels
                , DirectDebitsFraManager.class,
                "getIncomingInvoicesPayableByDirectDebit"
        );
    }

    protected List<DirectDebitInvoiceViewModel> doGetDirectDebitInvoiceViewModels() {
        final List<IncomingInvoice> invoices = incomingInvoiceRepository
                .findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                        AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE, PaymentMethod.DIRECT_DEBIT);
        return invoices.stream()
                .map(DirectDebitInvoiceViewModel::new)
                .sorted()
                .collect(Collectors.toList());
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public DirectDebitsFraManager markAsPaid(
            @Nullable // workaround for a bug in framework, whereby select drop-down goes top-left on error
            final List<DirectDebitInvoiceViewModel> invoices,
            @Nullable
            final String comment) {

        if(invoices == null || invoices.isEmpty()) {
            return this;
        }

        for (DirectDebitInvoiceViewModel viewModel : invoices) {
            factoryService.mixin(IncomingInvoice_markAsPaidByDirectDebit.class, viewModel.getIncomingInvoice())
                          .act(comment);
        }

        return this;
    }

    public String disableMarkAsPaid() {
        return choices0MarkAsPaid().isEmpty() ? "No invoices" : null;
    }

    public List<DirectDebitInvoiceViewModel> choices0MarkAsPaid() {
        return getIncomingInvoicesPayableByDirectDebit();
    }

    public String validate0MarkAsPaid(List<DirectDebitInvoiceViewModel> viewModels) {
        return viewModels == null || viewModels.isEmpty() ? "Select one or more invoices to mark as paid" : null;
    }




    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    FactoryService factoryService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE) QueryResultsCache queryResultsCache;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    IncomingInvoiceRepository incomingInvoiceRepository;

}
