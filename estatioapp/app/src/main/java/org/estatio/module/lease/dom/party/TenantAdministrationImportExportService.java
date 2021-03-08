package org.estatio.module.lease.dom.party;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.api.client.util.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

@DomainService(nature = NatureOfService.DOMAIN, objectType = "party.TenantAdministrationImportExportService")
public class TenantAdministrationImportExportService {

    public Blob export(){
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

    @Inject ExcelService excelService;

    @Inject TenantAdministrationRecordRepository tenantAdministrationRecordRepository;

}
