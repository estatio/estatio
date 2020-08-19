package org.estatio.module.lease.app;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.imports.SalesAreaLicenseImport;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.module.lease.app.SalesAreaMenu"
)
public class SalesAreaMenu {

    public Blob downloadSalesAreaLicences(final Property property, @Nullable final String fileName){
        List<SalesAreaLicenseImport> lines = new ArrayList<>();
        final List<Lease> leasesActive6MonthsAgo = leaseRepository.findByAssetAndActiveOnDate(property, clockService.now().minusMonths(6));
        final List<Lease> leasesActiveNow = leaseRepository.findByAssetAndActiveOnDate(property, clockService.now());
        List<Lease> leases = new ArrayList<>();
        leases.addAll(leasesActive6MonthsAgo);
        leases.addAll(leasesActiveNow);
        for (Lease l : leases.stream().distinct().sorted(Comparator.comparing(Lease::getReference)).collect(Collectors.toList())) {
            for (Occupancy o : l.getOccupancies()){
                if (o.getCurrentSalesAreaLicense()!=null){
                    lines.add(new SalesAreaLicenseImport(o.getCurrentSalesAreaLicense()));
                } else {
                    lines.add(new SalesAreaLicenseImport(o));
                }
            }
        }
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "SalesAreaLicences-" + property.getReference();
            fileNameToUse = fileNameToUse + "-" +  clockService.now().toString() +".xlsx";
        } else {
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                fileNameToUse = fileName.concat(".xlsx");
            } else {
                fileNameToUse = fileName;
            }
        }
        return excelService.toExcel(lines, SalesAreaLicenseImport.class, "lines", fileNameToUse);
    }

    public void uploadSalesAreaLicences(final Blob sheet){
        final List<SalesAreaLicenseImport> lines = excelService.fromExcel(sheet, SalesAreaLicenseImport.class, "lines");
        lines.forEach(l->l.importData());
    }

    @Inject LeaseRepository leaseRepository;

    @Inject ExcelService excelService;

    @Inject ClockService clockService;

}
