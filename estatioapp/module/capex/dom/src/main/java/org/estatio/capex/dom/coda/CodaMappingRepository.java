package org.estatio.capex.dom.coda;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.charge.Charge;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaMapping.class)
public class CodaMappingRepository extends UdoDomainRepositoryAndFactory<CodaMapping> {

    public CodaMappingRepository() {
        super(CodaMappingRepository.class, CodaMapping.class);
    }

    List<CodaMapping> allMappings() {
        return allInstances();
    }

    @Programmatic
    public CodaMapping findOrCreate(
            String atPath,
            DocumentType documentType,
            final IncomingInvoiceType incomingInvoiceType, CodaTransactionType codaTransactionType,
            Charge charge,
            boolean propertyIsFullyOwned,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            LocalDate startDate,
            LocalDate endDate,
            CodaElement codaElement
    ) {
        final CodaMapping codaMapping = findByAll(
                atPath,
                documentType,
                incomingInvoiceType,
                codaTransactionType,
                charge,
                propertyIsFullyOwned,
                periodStartDate,
                periodEndDate,
                startDate,
                endDate,
                codaElement);
        if (codaMapping != null)
            return codaMapping;
        return create(
                atPath,
                documentType,
                incomingInvoiceType,
                codaTransactionType,
                charge,
                propertyIsFullyOwned,
                periodStartDate,
                periodEndDate,
                startDate,
                endDate, codaElement);
    }

    @Programmatic
    public CodaMapping findByAll(
            final String atPath,
            final DocumentType documentType,
            final IncomingInvoiceType incomingInvoiceType,
            final CodaTransactionType codaTransactionType,
            final Charge charge,
            final boolean propertyIsFullyOwned,
            final LocalDate periodStartDate,
            final LocalDate periodEndDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final CodaElement codaElement
    ) {
        return uniqueMatch("findByAll",
                "atPath", atPath,
                "documentType", documentType,
                "incomingInvoiceType", incomingInvoiceType,
                "codaTransactionType", codaTransactionType,
                "charge", charge,
                "propertyIsFullyOwned", propertyIsFullyOwned,
                "periodStartDate", periodStartDate,
                "periodEndDate", periodEndDate,
                "startDate", startDate,
                "endDate", endDate,
                "codaElement", codaElement);
    }

    private CodaMapping create(
            final String atPath,
            final DocumentType documentType,
            final IncomingInvoiceType incomingInvoiceType, CodaTransactionType codaTransactionType,
            final Charge charge,
            final boolean propertyIsFullyOwned,
            final LocalDate periodStartDate,
            final LocalDate periodEndDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final CodaElement codaElement
    ) {
        CodaMapping codaMapping = newTransientInstance();
        codaMapping.setAtPath(atPath);
        codaMapping.setDocumentType(documentType);
        codaMapping.setIncomingInvoiceType(incomingInvoiceType);
        codaMapping.setCodaTransactionType(codaTransactionType);
        codaMapping.setCharge(charge);
        codaMapping.setPropertyIsFullyOwned(propertyIsFullyOwned);
        codaMapping.setPeriodStartDate(periodStartDate);
        codaMapping.setPeriodEndDate(periodEndDate);
        codaMapping.setCodaElement(codaElement);
        codaMapping.setStartDate(startDate);
        codaMapping.setEndDate(endDate);
        persistIfNotAlready(codaMapping);
        return null;
    }

    public List<CodaMapping> findByCharge(final Charge charge) {
        return isisJdoSupport.executeQuery(
                CodaMapping.class,
                QCodaMapping.candidate()
                        .charge.eq(charge))
                .stream().collect(Collectors.toList());
    }

    public List<CodaMapping> findByCodaElement(final CodaElement codaElement) {
        return isisJdoSupport.executeQuery(
                CodaMapping.class,
                QCodaMapping.candidate()
                        .codaElement.eq(codaElement))
                .stream().collect(Collectors.toList());
    }

    public List<CodaMapping> findMatching(
            final IncomingInvoiceType incomingInvoiceType,
            final Charge charge
    ) {
        final QCodaMapping q = QCodaMapping.candidate();
        return isisJdoSupport.executeQuery(
                CodaMapping.class,
                q.charge.eq(charge)
                        .and(q.incomingInvoiceType.eq(incomingInvoiceType))
        ).stream().collect(Collectors.toList());
    }

    @Inject IsisJdoSupport isisJdoSupport;

    public List<CodaMapping> all() {
        return allInstances();
    }
}
