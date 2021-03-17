package org.estatio.module.fastnet.dom;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.util.Mode;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = RentRollLine.class
)
public class RentRollLineRepository {

    // NOTE : no creator method in this repo because the only way objects get instantiated is by excelservice

    @Programmatic
    public List<RentRollLine> listAll() {
        return repositoryService.allInstances(RentRollLine.class);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    public List<LocalDate> findUniqueExportDates() {
        List startDates = repositoryService.allMatches(
                new QueryDefault<>(
                        RentRollLine.class,
                        "findUniqueExportDates"));

        return startDates;
    }

    @Programmatic
    public List<RentRollLine> findByKeyToLeaseExternalReference(final String keyToLeaseExternalReference) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        RentRollLine.class,
                        "findByKeyToLeaseExternalReference",
                        "keyToLeaseExternalReference", keyToLeaseExternalReference));
    }

    @Programmatic
    public RentRollLine findByObjektsNummerAndKontraktNrAndEvdInSd(final String objektsNummer, final String kontraktNr, final LocalDateTime evdInSd) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        RentRollLine.class,
                        "findByObjektsNummerAndKontraktNrAndEvdInSd",
                        "objektsNummer", objektsNummer,
                        "kontraktNr", kontraktNr,
                        "evdInSd", evdInSd));
    }

    @Programmatic
    public List<RentRollLine> findByExportDate(final LocalDate exportDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        RentRollLine.class,
                        "findByExportDate",
                        "exportDate", exportDate));
    }

    @Programmatic
    public List<RentRollLine> importRentRollItems(final Blob spreadsheet) {
        List<RentRollLine> rentRollItems =
                excelService.fromExcel(spreadsheet, RentRollLine.class, "Actual_rentroll", Mode.RELAXED);
        rentRollItems.forEach(x -> {
            serviceRegistry2.injectServicesInto(x);
            x.setFutureRentRollLine(false);
            x.importData(null);
        });
        return rentRollItems;
    }

    @Programmatic
    public List<RentRollLine> importFutureRentRollItems(final Blob spreadsheet) {
        List<RentRollLine> futureRentRollItems =
                excelService.fromExcel(spreadsheet, RentRollLine.class, "Future_Rentroll", Mode.RELAXED);
        futureRentRollItems.forEach(x -> {
            serviceRegistry2.injectServicesInto(x);
            x.setFutureRentRollLine(true);
            x.importData(null);
        });
        return futureRentRollItems;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    ExcelService excelService;
}
