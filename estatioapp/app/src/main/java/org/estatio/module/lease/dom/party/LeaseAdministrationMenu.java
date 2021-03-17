package org.estatio.module.lease.dom.party;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;


@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "party.LeaseAdministrationMenu"
)
public class LeaseAdministrationMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public List<TenantAdministrationRecordVM> importTenantAdministrationRecords(final Blob sheet){
        return tenantAdministrationImportExportService.importTenantAdministrationRecords(sheet);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob exportTenantAdministrationRecords(){
        return tenantAdministrationImportExportService.exportTenantAdministrationRecords();
    }

    @Inject TenantAdministrationImportExportService tenantAdministrationImportExportService;

}
