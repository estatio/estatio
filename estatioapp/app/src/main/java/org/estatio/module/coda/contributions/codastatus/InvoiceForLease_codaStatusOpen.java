package org.estatio.module.coda.contributions.codastatus;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.linking.DeepLinkService;

import org.isisaddons.module.publishmq.dom.jdo.status.StatusMessage;
import org.isisaddons.module.publishmq.dom.jdo.status.StatusMessageRepository;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="act")
public class InvoiceForLease_codaStatusOpen {


    private final InvoiceForLease invoice;

    public InvoiceForLease_codaStatusOpen(InvoiceForLease invoice) {
        this.invoice = invoice;
    }

    /**
     * Opens up the StatusMessage, if any.
     */
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act() {
        final Optional<StatusMessage> statusMessage = findStatusMessage();
        return statusMessage.orElse(null);
    }

    public boolean hideAct() {
        return !findStatusMessage().isPresent();
    }

    private Optional<StatusMessage> findStatusMessage() {
        final StatusMessageSummary summary = statusMessageSummaryCache.findFor(invoice);
        if(summary == null) {
            return Optional.empty();
        }

        // TODO: could improve by pushing filtering of timestamp into the StatusMessageRepository
        final List<StatusMessage> statusMessagesForTransaction =
                statusMessageRepository.findByTransactionId(summary.getTransactionId());
        return statusMessagesForTransaction
                .stream()
                .filter(x -> Objects.equals(x.getTimestamp(), summary.getTimestamp()))
                .findFirst();
    }

    /**
     * Attempts to convert into a URL so will open in new tab.
     */
    private Object asUrl(final StatusMessage statusMessage) {
        try {
            final URI uri = deepLinkService.deepLinkFor(statusMessage);
            return uri.toURL();
        } catch (Exception e) {
            return statusMessage;
        }
    }


    @Inject
    DeepLinkService deepLinkService;
    @Inject
    StatusMessageSummaryCache statusMessageSummaryCache;
    @Inject
    StatusMessageRepository statusMessageRepository;

}
