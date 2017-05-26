package org.estatio.capex.dom.coda;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.charge.Charge;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaMapping.class)
public class CodaMappingRepository extends UdoDomainRepositoryAndFactory<CodaMapping> {

    public CodaMappingRepository() {
        super(CodaMappingRepository.class, CodaMapping.class);
    }

    @Programmatic
    public CodaMapping findOrCreate(
            String atPath,
            DocumentType documentType,
            CodaTransactionType codaTransactionType,
            Charge charge,
            CodaMappingFilter projectFilter,
            CodaMappingFilter propertyFilter,
            CodaMappingFilter budgetFilter,
            boolean propertyIsFullyOwned,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            LocalDate startDate,
            LocalDate endDate,
            CodaElement codaElement
    ) {
        final CodaMapping codaMapping = findByAlllllllll(atPath, documentType, codaTransactionType, charge, projectFilter, propertyFilter, budgetFilter, propertyIsFullyOwned, periodStartDate, periodEndDate, startDate, endDate, codaElement);
        if (codaMapping != null)
            return codaMapping;
        return create(atPath, documentType, codaTransactionType, charge, projectFilter, propertyFilter, budgetFilter, propertyIsFullyOwned, periodStartDate, periodEndDate, startDate, endDate, codaElement);
    }

    @Programmatic
    public CodaMapping findByAlllllllll(
            String atPath,
            DocumentType documentType,
            CodaTransactionType codaTransactionType,
            Charge charge,
            CodaMappingFilter projectFilter,
            CodaMappingFilter propertyFilter,
            CodaMappingFilter budgetFilter,
            boolean propertyIsFullyOwned,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            LocalDate startDate,
            LocalDate endDate,
            CodaElement codaElement
    ) {
        return uniqueMatch("findByAlllllllll",
                "atPath", atPath,
                "documentType", documentType,
                "codaTransactionType", codaTransactionType,
                "charge", charge,
                "projectFilter", projectFilter,
                "propertyFilter", propertyFilter,
                "budgetFilter", budgetFilter,
                "propertyIsFullyOwned", propertyIsFullyOwned,
                "periodStartDate", periodStartDate,
                "periodEndDate", periodEndDate,
                "startDate", startDate,
                "endDate", endDate,
                "codaElement", codaElement);
        }

    private CodaMapping create(
            String atPath,
            DocumentType documentType,
            CodaTransactionType codaTransactionType,
            Charge charge,
            CodaMappingFilter projectFilter,
            CodaMappingFilter propertyFilter,
            CodaMappingFilter budgetFilter,
            boolean propertyIsFullyOwned,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            LocalDate startDate,
            LocalDate endDate,
            CodaElement codaElement
    ){
        CodaMapping codaMapping = newTransientInstance();
        codaMapping.setAtPath(atPath);
        codaMapping.setDocumentType(documentType);
        codaMapping.setCodaTransactionType(codaTransactionType);
        codaMapping.setBudgetFilter(budgetFilter);
        codaMapping.setCharge(charge);
        codaMapping.setProjectFilter(projectFilter);
        codaMapping.setPropertyFilter(propertyFilter);
        codaMapping.setPropertyIsFullyOwned(propertyIsFullyOwned);
        codaMapping.setPeriodStartDate(periodStartDate);
        codaMapping.setPeriodEndDate(periodEndDate);
        codaMapping.setCodaElement(codaElement);
        codaMapping.setStartDate(startDate);
        codaMapping.setEndDate(endDate);
        persistIfNotAlready(codaMapping);
        return null;
    }
}
