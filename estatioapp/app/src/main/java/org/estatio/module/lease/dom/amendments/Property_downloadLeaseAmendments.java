package org.estatio.module.lease.dom.amendments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

@Mixin
public class Property_downloadLeaseAmendments {

    private final Property property;

    public Property_downloadLeaseAmendments(Property property) {
        this.property = property;
    }

    @Action()
    public Blob $$(@Nullable final String fileName) {
        List<LeaseAmendmentImportLine> lines = new ArrayList<>();
        for (Lease lease : leaseRepository.findLeasesByProperty(property)) {
            for (LeaseAmendment amendment : leaseAmendmentRepository.findByLease(lease)){
                lines.add(new LeaseAmendmentImportLine(amendment));
            }
        }
        String fileNameToUse;
        if (fileName==null) {
            fileNameToUse = "Amendments-" + property.getReference() + "-" + clockService.now().toString() +".xlsx";
        } else {
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                fileNameToUse = fileName.concat(".xlsx");
            } else {
                fileNameToUse = fileName;
            }
        }
        return excelService.toExcel(lines.stream().sorted(Comparator.comparing(LeaseAmendmentImportLine::getLeaseReference)).collect(
                Collectors.toList()), LeaseAmendmentImportLine.class, "lines", fileNameToUse);
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

}
