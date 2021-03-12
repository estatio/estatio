package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.*;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.party.ContinuationPlan "
                        + "WHERE tenantAdministrationRecord == :tenantAdministrationRecord "),
})
@Unique(name = "ContinuationPlan_tenantAdministrationRecord_UNQ", members = {"tenantAdministrationRecord"})
@DomainObject(objectType = "party.ContinuationPlan")
public class ContinuationPlan {

    public String title(){
        return TitleBuilder.start().withParent(getTenantAdministrationRecord()).withName("continuation plan").toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "tenantAdministrationRecordId")
    private TenantAdministrationRecord tenantAdministrationRecord;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate judgmentDate;

    @Persistent(mappedBy = "continuationPlan", dependentElement = "true")
    @Getter @Setter
    private SortedSet<ContinuationPlanEntry> entries = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public ContinuationPlanEntry addEntry(final LocalDate date, final BigDecimal percentage){
        return continuationPlanEntryRepository.upsert(this, date, percentage);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob exportEntries(){
        return tenantAdministrationImportExportService.exportEntries(this);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob exportEntriesSample(final List<BigDecimal> percentages){
        return tenantAdministrationImportExportService.exportEntriesSample(this, percentages);
    }

    public List<BigDecimal> default0ExportEntriesSample() {
        return new ArrayList<BigDecimal>(Arrays.asList(
                new BigDecimal("3"),
                new BigDecimal("7"),
                new BigDecimal("11"),
                new BigDecimal("11"),
                new BigDecimal("11"),
                new BigDecimal("11"),
                new BigDecimal("11"),
                new BigDecimal("11"),
                new BigDecimal("12"),
                new BigDecimal("12")));
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public List<ContinuationPlanEntryVM> importEntries(final Blob sheet){
        return tenantAdministrationImportExportService.importContinuationPlanEntries(sheet);
    }

    @Inject
    ContinuationPlanEntryRepository continuationPlanEntryRepository;

    @Inject
    TenantAdministrationImportExportService tenantAdministrationImportExportService;

}
