package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.Lease;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = TenantAdministrationLeaseDetails.class,
        objectType = "party.TenantAdministrationLeaseDetailsRepository")
public class TenantAdministrationLeaseDetailsRepository {

    public TenantAdministrationLeaseDetails findUnique(final TenantAdministrationRecord tenantAdministrationRecord, final
            Lease lease){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        TenantAdministrationLeaseDetails.class,
                        "findUnique",
                        "tenantAdministrationRecord", tenantAdministrationRecord,
                        "lease", lease));
    }

    public TenantAdministrationLeaseDetails upsert(
            final TenantAdministrationRecord record,
            final Lease lease,
            final BigDecimal declaredAmountOfClaim,
            final Boolean debtAdmitted,
            final BigDecimal admittedAmountOfClaim,
            final Boolean leaseContinued
    ){
        final TenantAdministrationLeaseDetails unique = findUnique(record, lease);
        if (unique !=null){
            unique.setDeclaredAmountOfClaim(declaredAmountOfClaim);
            unique.setDebtAdmitted(debtAdmitted);
            unique.setAdmittedAmountOfClaim(admittedAmountOfClaim);
            unique.setLeaseContinued(leaseContinued);
            return unique;
        } else {
            return create(record, lease, declaredAmountOfClaim, debtAdmitted, admittedAmountOfClaim, leaseContinued);
        }
    }

    private TenantAdministrationLeaseDetails create(
            final TenantAdministrationRecord record,
            final Lease lease,
            final BigDecimal declaredAmountOfClaim,
            final Boolean debtAdmitted,
            final BigDecimal admittedAmountOfClaim,
            final Boolean leaseContinued
            ){
        TenantAdministrationLeaseDetails details = new TenantAdministrationLeaseDetails();
        details.setTenantAdministrationRecord(record);
        details.setLease(lease);
        details.setDeclaredAmountOfClaim(declaredAmountOfClaim);
        details.setDebtAdmitted(debtAdmitted);
        details.setAdmittedAmountOfClaim(admittedAmountOfClaim);
        details.setLeaseContinued(leaseContinued);
        repositoryService.persistAndFlush(details);
        return details;
    }

    public List<TenantAdministrationLeaseDetails> listAll(){
        return repositoryService.allInstances(TenantAdministrationLeaseDetails.class);
    }

    @Inject
    RepositoryService repositoryService;

}
