package org.estatio.module.coda.contributions.codadocument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLinkRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleAmendmentItemLinkRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleInvoiceLinkRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleLeaseItemLinkRepository;

@Mixin
public class AmortisationSchedule_delete {

    private final AmortisationSchedule amortisationSchedule;

    public AmortisationSchedule_delete(AmortisationSchedule amortisationSchedule) {
        this.amortisationSchedule = amortisationSchedule;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Lease $$() {

        final Lease lease = amortisationSchedule.getLease();
        List<CodaDocument> docsToDelete = new ArrayList<>();
        // add all coda docs related to entry
        Lists.newArrayList(amortisationSchedule.getEntries())
                .forEach(e->{
                    docsToDelete.addAll(codaDocumentLinkRepository.findByAmortisationEntry(e).stream()
                            .map(l->l.getCodaDocumentLine().getDocument())
                            .distinct()
                            .collect(Collectors.toList()));
                });
        // add all coda docs related to schedule
        docsToDelete.addAll(codaDocumentLinkRepository.findByAmortisationSchedule(amortisationSchedule).stream()
                .map(l->l.getCodaDocumentLine().getDocument())
                .distinct()
                .collect(Collectors.toList()));
        docsToDelete.stream().distinct().forEach(d->factoryService.mixin(CodaDocument_delete.class, d).$$());
        // delete all links to schedule
        amortisationScheduleLeaseItemLinkRepository.findBySchedule(amortisationSchedule).forEach(l->{
            repositoryService.removeAndFlush(l);
        });
        amortisationScheduleAmendmentItemLinkRepository.findBySchedule(amortisationSchedule).forEach(l->{
            repositoryService.removeAndFlush(l);
        });
        amortisationScheduleInvoiceLinkRepository.findBySchedule(amortisationSchedule).forEach(l->{
            repositoryService.removeAndFlush(l);
        });

        repositoryService.removeAndFlush(amortisationSchedule);

        return lease;
    }

    public String disable$$(){
        if (amortisationSchedule.isReported()) return "This schedule is reported";
        return null;
    }

    @Inject RepositoryService repositoryService;

    @Inject AmortisationScheduleLeaseItemLinkRepository amortisationScheduleLeaseItemLinkRepository;

    @Inject AmortisationScheduleAmendmentItemLinkRepository amortisationScheduleAmendmentItemLinkRepository;

    @Inject AmortisationScheduleInvoiceLinkRepository amortisationScheduleInvoiceLinkRepository;

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

    @Inject FactoryService factoryService;

}
