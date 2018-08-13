package org.estatio.module.coda.contributions.codastatus;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.WithTransactionScope;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.core.metamodel.services.jdosupport.Persistable_datanucleusIdLong;

import org.isisaddons.module.publishmq.dom.jdo.status.StatusMessage;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

import lombok.Getter;
import lombok.Setter;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class StatusMessageSummaryCache implements WithTransactionScope {

    public static class InvoiceIdAndStatusMessageSummary {
        @Getter @Setter
        private long invoiceId;

        /**
         * From the corresponding {@link StatusMessage}, but may be null
         *
         * <p>
         *     can't use UUID - according to http://www.datanucleus.org/products/accessplatform/jdo/query.html#sql_candidate, not a valid type.
         * </p>
         */
        @Getter @Setter
        private String smTransactionId;

        /**
         * From the corresponding {@link StatusMessage}, but may be null
         */
        @Getter @Setter
        private Timestamp smTimestamp;

        /**
         * From the corresponding {@link StatusMessage}, but may be null
         */
        @Getter @Setter
        private String smMessage;
    }

    private final Map<Long, Optional<StatusMessageSummary>> statusMessageByInvoiceId = Maps.newHashMap();

    @Programmatic
    public StatusMessageSummary findFor(final InvoiceForLease invoice) {
        final Long invoiceId = idFor(invoice);

        Optional<StatusMessageSummary> statusMessageOpt = statusMessageByInvoiceId.get(invoiceId);
        if (statusMessageOpt == null) {
            final List<InvoiceIdAndStatusMessageSummary> statusMessages = find(invoiceId);

            final Map<Long, Optional<StatusMessageSummary>> map = statusMessages.stream()
                    .collect(Collectors.toMap(
                                InvoiceIdAndStatusMessageSummary::getInvoiceId,
                                StatusMessageSummaryCache::optionallyRecreateFrom));
            statusMessageByInvoiceId.putAll(map);

            statusMessageOpt = statusMessageByInvoiceId.get(invoiceId);
        }
        return statusMessageOpt.orElse(null);
    }

    private List<InvoiceIdAndStatusMessageSummary> find(final Long invoiceId) {
        final PersistenceManager jdoPersistenceManager = isisJdoSupport.getJdoPersistenceManager();

        final String sql = readSql();
        final Query query = jdoPersistenceManager.newQuery("javax.jdo.query.SQL", sql);
        query.setResultClass(InvoiceIdAndStatusMessageSummary.class);

        return (List<InvoiceIdAndStatusMessageSummary>) query.executeWithMap(ImmutableMap.of("invoiceId", invoiceId));
    }

    private Long idFor(final Object object) {
        return factoryService.mixin(Persistable_datanucleusIdLong.class, object).prop();
    }

    private String readSql() {
        final URL resource = Resources.getResource(getClass(), "StatusMessageSummaryCache~findFor.sql");
        final String s;
        try {
            s = Resources.toString(resource, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    private static Optional<StatusMessageSummary> optionallyRecreateFrom(final InvoiceIdAndStatusMessageSummary iiasms) {
        return Optional.ofNullable(
                iiasms.getSmTimestamp() != null
                        ? recreateFrom(iiasms)
                        : null);
    }

    private static StatusMessageSummary recreateFrom(final InvoiceIdAndStatusMessageSummary iiasms) {
        final StatusMessageSummary sm = new StatusMessageSummary();
        sm.setMessage(iiasms.getSmMessage());
        sm.setTransactionId(UUID.fromString(iiasms.getSmTransactionId()));
        sm.setTimestamp(iiasms.getSmTimestamp());
        return sm;
    }

    @Override
    public void resetForNextTransaction() {
        statusMessageByInvoiceId.clear();
    }


    @Inject
    IsisJdoSupport isisJdoSupport;
    @Inject
    FactoryService factoryService;
}
