package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.api.client.util.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

@DomainService(nature = NatureOfService.DOMAIN, objectType = "party.TenantAdministrationImportExportService")
public class TenantAdministrationImportExportService {

    public Blob exportTenantAdministrationRecords(){
        List<TenantAdministrationRecordVM> vms = new ArrayList<>();

        final List<TenantAdministrationRecord> tenantAdministrationRecords = tenantAdministrationRecordRepository
                .listAll();
        tenantAdministrationRecords
                .forEach(r->{
                    final SortedSet<TenantAdministrationLeaseDetails> leaseDetails = r.getLeaseDetails();
                    if (leaseDetails.isEmpty()){
                        TenantAdministrationRecordVM vm = new TenantAdministrationRecordVM(
                                r.getTenant().getReference(),
                                r.getStatus().toString(),
                                r.getJudicialRedressDate(),
                                r.getLiquidationDate(),
                                r.getComments(),
                                null, null, null, null,null);
                        vms.add(vm);
                    } else {
                        Lists.newArrayList(leaseDetails).forEach(ld -> {
                            TenantAdministrationRecordVM vm = new TenantAdministrationRecordVM(
                                    r.getTenant().getReference(),
                                    r.getStatus().toString(),
                                    r.getJudicialRedressDate(),
                                    r.getLiquidationDate(),
                                    r.getComments(),
                                    ld.getLease().getReference(),
                                    ld.getDeclaredAmountOfClaim(),
                                    ld.getDebtAdmitted(),
                                    ld.getAdmittedAmountOfClaim(),
                                    ld.getLeaseContinued());
                            vms.add(vm);
                        });
                    }
                });

        return excelService.toExcel(vms, TenantAdministrationRecordVM.class, "records", "TenantAdministrationExport.xlsx");
    }

    public List<TenantAdministrationRecordVM> importTenantAdministrationRecords(final Blob sheet){
        final List<TenantAdministrationRecordVM> vms = excelService
                .fromExcel(sheet, TenantAdministrationRecordVM.class, "records");
        vms.forEach(vm->vm.importData(null));
        return vms;
    }

    public Blob exportEntries(final ContinuationPlan continuationPlan){
        List<ContinuationPlanEntryVM> vms = new ArrayList<>();
        Lists.newArrayList(continuationPlan.getEntries()).forEach(e->{
            final SortedSet<EntryValueForLease> entryValues = e.getEntryValues();
            if (entryValues.isEmpty()){
                vms.add(new ContinuationPlanEntryVM(
                        continuationPlan.getTenantAdministrationRecord().getTenant().getReference(),
                        e.getDate(),
                        e.getPercentage(),
                        null,
                        null,
                        null
                ));
            } else {
                Lists.newArrayList(entryValues).forEach(v->{
                    vms.add(new ContinuationPlanEntryVM(
                        continuationPlan.getTenantAdministrationRecord().getTenant().getReference(),
                        e.getDate(),
                        e.getPercentage(),
                        v.getLeaseDetails().getLease().getReference(),
                        v.getAmount(),
                        v.getDatePaid()
                    ));
                });

            }
        });
        return excelService.toExcel(vms, ContinuationPlanEntryVM.class, "entries", "ContinuationPlanExport.xlsx");
    }

    public Blob exportEntriesSample(final ContinuationPlan continuationPlan){
        final SortedSet<TenantAdministrationLeaseDetails> leaseDetails = continuationPlan
                .getTenantAdministrationRecord().getLeaseDetails();
        final List<String> leaseRefs = Lists.newArrayList(leaseDetails).stream()
                .map(ld -> ld.getLease().getReference()).collect(Collectors.toList());
        final List<BigDecimal> leaseAmounts = Lists.newArrayList(leaseDetails).stream()
                .map(TenantAdministrationLeaseDetails::getAdmittedAmountOfClaim)
                .collect(Collectors.toList());
        final List<ContinuationPlanEntry> entries = Lists.newArrayList(continuationPlan.getEntries());
        List<ContinuationPlanEntryVM> vms = new ArrayList<>();
        for (int i = 0; i < leaseRefs.size() ; i++) {
            for (ContinuationPlanEntry entry : entries) {
                ContinuationPlanEntryVM vm = new ContinuationPlanEntryVM(
                        continuationPlan.getTenantAdministrationRecord().getTenant().getReference(),
                        entry.getDate(),
                        entry.getPercentage(),
                        leaseRefs.get(i),
                        entryValueForLeaseRepository.calculateAmount(leaseAmounts.get(i), entry.getPercentage()),
                        null
                );
                vms.add(vm);
            }
        }
        return excelService.toExcel(vms, ContinuationPlanEntryVM.class, "entries", "ContinuationPlanExport.xlsx");
    }

    public List<ContinuationPlanEntryVM> importContinuationPlanEntries(final Blob sheet){
        final List<ContinuationPlanEntryVM> entries = excelService
                .fromExcel(sheet, ContinuationPlanEntryVM.class, "entries");
        entries.forEach(e->e.importData(null));
        return entries;
    }

    @Inject ExcelService excelService;

    @Inject TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

    @Inject EntryValueForLeaseRepository entryValueForLeaseRepository;

}
