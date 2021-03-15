package org.estatio.module.coda.dom.doc;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.coda.contributions.IncomingInvoice_codaDocHead;
import org.estatio.module.invoice.dom.PaymentMethod;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocHead.class,
        objectType = "coda.CodaDocHeadRepository"
)
public class CodaDocHeadRepository {

    static final String STAT_PAY_PAID = "paid";
    static final String STAT_PAY_AVAILABLE = "available";

    @Programmatic
    public java.util.List<CodaDocHead> listAll() {
        return repositoryService.allInstances(CodaDocHead.class);
    }

    @Programmatic
    public CodaDocHead findByCmpCodeAndDocCodeAndDocNum(
            final String cmpCode,
            final String docCode,
            final String docNum
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaDocHead.class,
                        "findByCmpCodeAndDocCodeAndDocNum",
                        "cmpCode", cmpCode,
                        "docCode", docCode,
                        "docNum", docNum));
    }

    @Programmatic
    public CodaDocHead findByCandidate(
            final CodaDocHead codaDocHead
    ) {
        final String cmpCode = codaDocHead.getCmpCode();
        final String docCode = codaDocHead.getDocCode();
        final String docNum = codaDocHead.getDocNum();
        return findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum);
    }

    @Programmatic
    public CodaDocHead persistAsReplacementIfRequired(final CodaDocHead codaDocHead) {
        // sanity check
        if(repositoryService.isPersistent(codaDocHead)) {
            throw new IllegalStateException(
                    String.format("CodaDocHead '%s' is already persistent", titleService.titleOf(codaDocHead)));
        }

        final CodaDocHead existingCodaDocHead = findByCandidate(codaDocHead);

        deriveStatPayPaidDateIfRequired(codaDocHead, existingCodaDocHead);

        if (existingCodaDocHead != null) {
            delete(existingCodaDocHead);
        }
        return repositoryService.persistAndFlush(codaDocHead);
    }

    void deriveStatPayPaidDateIfRequired(final CodaDocHead codaDocHead, final CodaDocHead existingCodaDocHead) {
        if (!STAT_PAY_PAID.equals(codaDocHead.getStatPay())) {
            return;
        }
        if (isPaid(existingCodaDocHead)) {
            codaDocHead.setStatPayPaidDate(existingCodaDocHead.getStatPayPaidDate());
            return;
        }
        // else
        codaDocHead.setStatPayPaidDate(clockService.now());
    }

    private static boolean isPaid(final CodaDocHead existingCodaDocHead) {
        if (existingCodaDocHead == null) {
            return false;
        }
        if (!STAT_PAY_PAID.equals(existingCodaDocHead.getStatPay())) {
            return false;
        }
        return existingCodaDocHead.getStatPayPaidDate() != null;
    }


    @Programmatic
    public List<CodaDocHead> findUnpaidAndInvalid() {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocHead.class,
                        "findByHandlingAndStatPayNotEqualToAndNotValid",
                        "statPay", STAT_PAY_PAID,
                        "handling", Handling.INCLUDED
                ));
    }

    @Programmatic
    public List<CodaDocHead> findAvailable() {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocHead.class,
                        "findByStatPay",
                        "statPay", STAT_PAY_AVAILABLE
                ));
    }

    @Programmatic
    public List<CodaDocHead> findByCmpCodeAndIncomingInvoiceApprovalStateIsNotFinal(final String cmpCode) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocHead.class,
                        "findByCmpCodeAndIncomingInvoiceApprovalStateIsNotFinal",
                        "cmpCode", cmpCode
                ));
    }

    @Programmatic
    public List<CodaDocHead> findByCodaPeriodQuarterAndHandlingAndValidity(
            final String codaPeriodQuarter,
            final Handling handling,
            final Validity validity) {
        switch (validity) {
        case VALID:
            return repositoryService.allMatches(
                    new org.apache.isis.applib.query.QueryDefault<>(
                            CodaDocHead.class,
                            "findByCodaPeriodQuarterAndHandlingAndValid",
                            "codaPeriodQuarter", codaPeriodQuarter,
                            "handling", handling));
        case NOT_VALID:
            return repositoryService.allMatches(
                    new org.apache.isis.applib.query.QueryDefault<>(
                            CodaDocHead.class,
                            "findByCodaPeriodQuarterAndHandlingAndNotValid",
                            "codaPeriodQuarter", codaPeriodQuarter,
                            "handling", handling));
        case BOTH:
        default:
            return repositoryService.allMatches(
                    new org.apache.isis.applib.query.QueryDefault<>(
                            CodaDocHead.class,
                            "findByCodaPeriodQuarterAndHandling",
                            "codaPeriodQuarter", codaPeriodQuarter,
                            "handling", handling));
        }
    }

    @Programmatic
    public CodaDocHead findByIncomingInvoice(final IncomingInvoice incomingInvoice) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        CodaDocHead.class,
                        "findByIncomingInvoice",
                        "incomingInvoice", incomingInvoice
                )
        );
    }

    public List<CodaDocHead> findByIncomingInvoiceAtPathPrefixAndApprovalState(
            final String atPathPrefix, final IncomingInvoiceApprovalState approvalState) {

        final List<CodaDocHead> codaDocHeads = repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocHead.class,
                        "findByIncomingInvoiceAtPathPrefixAndApprovalState",
                        "atPathPrefix", atPathPrefix,
                        "approvalState", approvalState));

        prepopulateQueryResultsCacheForMixin(codaDocHeads);

        return codaDocHeads;
    }

    public List<CodaDocHead> findByIncomingInvoiceAtPathPrefixAndApprovalStateAndPaymentMethod(
            final String atPathPrefix,
            final IncomingInvoiceApprovalState approvalState,
            final PaymentMethod paymentMethod) {

        final List<CodaDocHead> codaDocHeads = repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocHead.class,
                        "findByIncomingInvoiceAtPathPrefixAndApprovalStateAndPaymentMethod",
                        "atPathPrefix", atPathPrefix,
                        "approvalState", approvalState,
                        "paymentMethod", paymentMethod));

        prepopulateQueryResultsCacheForMixin(codaDocHeads);

        return codaDocHeads;
    }

    /**
     * populate the cache, so that IncomingInvoice_codaDocHead
     * (which will be called soon) doesn't need to run a query.
     *
     * @param codaDocHeads
     */
    private void prepopulateQueryResultsCacheForMixin(final List<CodaDocHead> codaDocHeads) {
        for (final CodaDocHead codaDocHead : codaDocHeads) {
            queryResultsCache.put(keyFor(codaDocHead.getIncomingInvoice()), codaDocHead);
        }
    }

    private QueryResultsCache.Key keyFor(final IncomingInvoice incomingInvoice) {
        return new QueryResultsCache.Key(IncomingInvoice_codaDocHead.class, "prop", incomingInvoice);
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Programmatic
    public boolean deleteIfNoInvoiceAttached(final CodaDocHead codaDocHead) {
        // sanity check, is already validated at the REST endpoint
        if (codaDocHead.getIncomingInvoice() == null) {
            delete(codaDocHead);
            return true;
        }

        return false;
    }

    private void delete(final CodaDocHead codaDocHead) {
        for (final CodaDocLine line : Lists.newArrayList(codaDocHead.getLines())) {
            repositoryService.removeAndFlush(line);
        }
        repositoryService.removeAndFlush(codaDocHead);
    }


    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    TitleService titleService;

    @Inject
    ClockService clockService;

}
