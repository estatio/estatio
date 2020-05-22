package org.estatio.module.lease.dom.amendments;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = LeaseAmendment.class
)
public class LeaseAmendmentRepository {

    @Programmatic
    public List<LeaseAmendment> listAll() {
        return repositoryService.allInstances(LeaseAmendment.class);
    }

    @Programmatic
    public List<LeaseAmendment> findByLease(final Lease lease) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findByLease",
                        "lease", lease));
    }

    @Programmatic
    public List<LeaseAmendment> findByState(final LeaseAmendmentState state) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findByState",
                        "state", state));
    }

    @Programmatic
    public LeaseAmendment findUnique(final Lease lease, final LeaseAmendmentType leaseAmendmentType) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findUnique",
                        "lease", lease,
                        "leaseAmendmentType", leaseAmendmentType));
    }

    public LeaseAmendment findByLeasePreview(final Lease leasePreview) {
        // Unique match since we allow max 1 preview per amendment
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findByLeasePreview",
                        "leasePreview", leasePreview));
    }

    @Programmatic
    public LeaseAmendment upsert(
            final Lease lease,
            final LeaseAmendmentType leaseAmendmentType,
            final LeaseAmendmentState state,
            final LocalDate startDate,
            final LocalDate endDate
    ){
        final LeaseAmendment amendment = findUnique(lease, leaseAmendmentType);
        LeaseAmendmentState stateToUse = state !=null ? state : LeaseAmendmentState.PROPOSED;
        if (amendment ==null){
            return create(lease, leaseAmendmentType, stateToUse, startDate, endDate);
        } else {
            amendment.setState(stateToUse);
            amendment.setStartDate(startDate);
            amendment.setEndDate(endDate);
            return amendment;
        }
    }

    @Programmatic
    public LeaseAmendment create(
            final Lease lease,
            final LeaseAmendmentType leaseAmendmentType,
            final LeaseAmendmentState state,
            final LocalDate startDate,
            final LocalDate endDate) {

        final LeaseAmendment leaseAmendment = new LeaseAmendment();
        leaseAmendment.setReference(lease.getReference().concat(leaseAmendmentType.getRef_suffix()));
        leaseAmendment.setName(lease.getReference().concat(leaseAmendmentType.getRef_suffix()));
        leaseAmendment.setType(agreementTypeRepository.find(LeaseAmendmentAgreementTypeEnum.LEASE_AMENDMENT));
        leaseAmendment.setLease(lease);
        leaseAmendment.setLeaseAmendmentType(leaseAmendmentType);
        leaseAmendment.setState(state);
        leaseAmendment.setStartDate(startDate);
        leaseAmendment.setEndDate(endDate);
        serviceRegistry2.injectServicesInto(leaseAmendment);
        repositoryService.persistAndFlush(leaseAmendment);

        final AgreementRoleType artLandlord = agreementRoleTypeRepository
                        .find(LeaseAgreementRoleTypeEnum.LANDLORD);
        leaseAmendment.newRole(artLandlord, lease.getPrimaryParty(), null ,null);
        final AgreementRoleType artTenant = agreementRoleTypeRepository
                .find(LeaseAgreementRoleTypeEnum.TENANT);
        leaseAmendment.newRole(artTenant, lease.getSecondaryParty(), null ,null);

        return leaseAmendment;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;
}
