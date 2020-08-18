package org.estatio.module.coda.subscribers;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaElementLevel;
import org.estatio.module.coda.dom.elements.CodaMapping;
import org.estatio.module.coda.dom.elements.CodaMappingRepository;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLinkRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "estatio.IncomingItemChargeSetSubscriber"
)
public class IncomingItemChargeSetSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void updateCodaElements(final IncomingInvoiceItem.ChargeSetEvent ev) {

        final IncomingInvoiceItem incomingInvoiceItem = ev.getSource();
        // TODO: ECP-1187: check with users which date to use as reference date
        final LocalDate referenceDate = incomingInvoiceItem.getInvoice().getInvoiceDate() != null ? incomingInvoiceItem.getInvoice().getInvoiceDate() : clockService.now();
        final List<CodaMapping> mappings = codaMappingRepository
                .findMatching(incomingInvoiceItem.getIncomingInvoiceType(), incomingInvoiceItem.getCharge());
        final CodaElement firstEl4OrElseNull = mappings.stream()
                .filter(m -> LocalDateInterval.including(m.getStartDate(), m.getEndDate()).contains(referenceDate))
                .filter(m -> m.getCodaElement().getLevel() == CodaElementLevel.LEVEL_4).findFirst()
                .map(m->m.getCodaElement())
                .orElse(null);
        final CodaElement firstEl5OrElseNull = mappings.stream()
                .filter(m -> LocalDateInterval.including(m.getStartDate(), m.getEndDate()).contains(referenceDate))
                .filter(m -> m.getCodaElement().getLevel() == CodaElementLevel.LEVEL_5).findFirst()
                .map(m->m.getCodaElement())
                .orElse(null);
        invoiceItemCodaElementsLinkRepository.upsert(incomingInvoiceItem, firstEl4OrElseNull, firstEl5OrElseNull);

    }

    @Inject
    CodaMappingRepository codaMappingRepository;

    @Inject
    InvoiceItemCodaElementsLinkRepository invoiceItemCodaElementsLinkRepository;

    @Inject
    ClockService clockService;


}
