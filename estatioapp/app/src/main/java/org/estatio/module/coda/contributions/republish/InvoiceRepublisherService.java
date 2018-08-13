package org.estatio.module.coda.contributions.republish;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.publishmq.dom.jdo.events.PublishedEvent;
import org.isisaddons.module.publishmq.dom.jdo.events.PublishedEventRepository;
import org.isisaddons.module.publishmq.dom.jdo.events.PublishedEvent_republish;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;


@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceRepublisherService {

    private final static Collection<String> memberIdentifiers =
            Lists.newArrayList(
                    "org.estatio.module.lease.dom.invoicing.InvoiceForLease$_collect#$$()",
                    "org.estatio.module.lease.dom.invoicing.InvoiceForLease$_invoice#$$()"
            );



    @Programmatic
    public void republishIfPresent(final InvoiceForLease invoice) {
        if (invoice == null) {
            return;
        }
        final PublishedEvent publishedEvent = findPublishedEvent(invoice);
        if(publishedEvent == null) {
            // the disableXxx guard below should be used to ensure there is, indeed, an event to republish
            return;
        }

        publish(publishedEvent);
    }

    @Programmatic
    public String disableRepublishIfPresent(final InvoiceForLease invoice) {
        final PublishedEvent publishedEvent = findPublishedEvent(invoice);
        return publishedEvent == null ? "No 'collect' or 'invoice' event found to republish" : null;
    }


    private PublishedEvent findPublishedEvent(final InvoiceForLease invoice) {
        return queryResultsCache.execute(
                () -> doFindPublishedEvent(invoice),
                InvoiceRepublisherService.class,
                "findPublishedEvent", invoice);
    }

    private PublishedEvent doFindPublishedEvent(final InvoiceForLease invoice) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(invoice);
        final List<PublishedEvent> recentByTarget =
                publishedEventRepository.findRecentByTarget(bookmark);
        recentByTarget.removeIf(x -> !memberIdentifiers.contains(x.getMemberIdentifier()));
        return recentByTarget.isEmpty() ? null : recentByTarget.get(0);
    }

    private void publish(final PublishedEvent publishedEvent) {
        final PublishedEvent_republish mixin = factoryService.mixin(PublishedEvent_republish.class, publishedEvent);
        wrapperFactory.wrap(mixin).$$();
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PublishedEventRepository publishedEventRepository;

    @Inject
    BookmarkService bookmarkService;

    @Inject
    WrapperFactory wrapperFactory;

    @Inject
    FactoryService factoryService;

}
