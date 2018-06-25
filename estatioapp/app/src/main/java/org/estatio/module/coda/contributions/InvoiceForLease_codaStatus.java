package org.estatio.module.coda.contributions;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.isisaddons.module.publishmq.dom.jdo.events.PublishedEvent;
import org.isisaddons.module.publishmq.dom.jdo.events.PublishedEventRepository;
import org.isisaddons.module.publishmq.dom.jdo.status.StatusMessage;
import org.isisaddons.module.publishmq.dom.jdo.status.StatusMessageRepository;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="act")
public class InvoiceForLease_codaStatus {

    private final static Collection<String> memberIdentifiers =
            Lists.newArrayList(
                    "org.estatio.module.ecpsync.contributions.UdoDomainObject2_sync#$$()",
                    "org.estatio.module.lease.dom.invoicing.InvoiceForLease$_collect#$$()",
                    "org.estatio.module.lease.dom.invoicing.InvoiceForLease$_invoice#$$()"
            );


    private final InvoiceForLease invoice;

    public InvoiceForLease_codaStatus(InvoiceForLease invoice) {
        this.invoice = invoice;
    }

    /**
     * Returns first status message of most recent published event, for specified member identifiers.
     *
     * <p>
     *     TODO: rework this naive implementation.
     * </p>
     */
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public StatusMessage act() {
        return queryResultsCache.execute(
                () -> findMostRecentStatusMessageIfAny(invoice),
                InvoiceForLease_codaStatus.class, "act", invoice);
    }

    private StatusMessage findMostRecentStatusMessageIfAny(final InvoiceForLease invoice) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(invoice);
        final List<PublishedEvent> recentByTarget =
                publishedEventRepository.findRecentByTarget(bookmark);
        recentByTarget.removeIf(x -> !memberIdentifiers.contains(x.getMemberIdentifier()));
        for (final PublishedEvent publishedEvent : recentByTarget) {
            final List<StatusMessage> statusMessages = statusMessageRepository
                    .findByTransactionId(publishedEvent.getTransactionId());
            if(!statusMessages.isEmpty()) {
                return statusMessages.get(0);
            }
        }
        return null;
    }

    @Inject
    QueryResultsCache queryResultsCache;
    @Inject
    PublishedEventRepository publishedEventRepository;
    @Inject
    StatusMessageRepository statusMessageRepository;

    @Inject
    BookmarkService bookmarkService;

}
