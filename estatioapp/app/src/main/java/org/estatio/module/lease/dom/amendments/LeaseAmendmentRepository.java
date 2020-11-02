package org.estatio.module.lease.dom.amendments;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.asset.dom.Property;
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
    public List<LeaseAmendment> findByType(final LeaseAmendmentTemplate leaseAmendmentTemplate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findByTemplate",
                        "leaseAmendmentTemplate", leaseAmendmentTemplate));
    }

    @Programmatic
    public List<LeaseAmendment> findByTypeAndState(final LeaseAmendmentTemplate leaseAmendmentTemplate, final LeaseAmendmentState state) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findByTemplateAndState",
                        "leaseAmendmentTemplate", leaseAmendmentTemplate,
                        "state", state));
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
    public LeaseAmendment findUnique(final Lease lease, final LeaseAmendmentTemplate leaseAmendmentTemplate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findUnique",
                        "lease", lease,
                        "leaseAmendmentTemplate", leaseAmendmentTemplate));
    }

    @Programmatic
    public LeaseAmendment findByReference(final String reference) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        LeaseAmendment.class,
                        "findByReference",
                        "reference", reference));
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
            final LeaseAmendmentTemplate leaseAmendmentTemplate,
            final LeaseAmendmentState state,
            final LocalDate startDate,
            final LocalDate endDate
    ){
        final LeaseAmendment amendment = findUnique(lease, leaseAmendmentTemplate);
        LeaseAmendmentState stateToUse = state !=null ? state : LeaseAmendmentState.PROPOSED;
        if (amendment ==null){
            return create(lease, leaseAmendmentTemplate, stateToUse, startDate, endDate);
        } else {
            if (amendment.getState()==LeaseAmendmentState.APPLIED) return amendment;
            amendment.setState(stateToUse);
            if (state==LeaseAmendmentState.SIGNED) amendment.setDateSigned(clockService.now());
            amendment.setStartDate(startDate);
            amendment.setEndDate(endDate);
            return amendment;
        }
    }

    @Programmatic
    public LeaseAmendment create(
            final Lease lease,
            final LeaseAmendmentTemplate leaseAmendmentTemplate,
            final LeaseAmendmentState state,
            final LocalDate startDate,
            final LocalDate endDate) {

        final LeaseAmendment leaseAmendment = new LeaseAmendment();
        leaseAmendment.setReference(lease.getReference().concat(leaseAmendmentTemplate.getRef_suffix()));
        leaseAmendment.setName(lease.getReference().concat(leaseAmendmentTemplate.getRef_suffix()));
        leaseAmendment.setType(agreementTypeRepository.find(LeaseAmendmentAgreementTypeEnum.LEASE_AMENDMENT));
        leaseAmendment.setLease(lease);
        leaseAmendment.setLeaseAmendmentTemplate(leaseAmendmentTemplate);
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

    @Programmatic
    public List<LeaseAmendment> findByTypeAndProperty(final LeaseAmendmentTemplate leaseAmendmentTemplate, final Property property){
        return findByType(leaseAmendmentTemplate).stream()
                .filter(a->a.getLease()!=null)
                .filter(a->a.getLease().getProperty()!=null)
                .filter(a->a.getLease().getProperty()==property)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<LeaseAmendment> findByTypeAndStateAndProperty(final LeaseAmendmentTemplate leaseAmendmentTemplate, final LeaseAmendmentState state, final Property property){
        return findByTypeAndState(leaseAmendmentTemplate, state).stream()
                .filter(a->a.getLease()!=null)
                .filter(a->a.getLease().getProperty()!=null)
                .filter(a->a.getLease().getProperty()==property)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<LeaseAmendment> findByProperty(final Property property){
        return listAll().stream()
                .filter(a->a.getLease()!=null)
                .filter(a->a.getLease().getProperty()!=null)
                .filter(a->a.getLease().getProperty()==property)
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<LeaseAmendment> findByPropertyAndState(final Property property, final LeaseAmendmentState state){
        return findByState(state).stream()
                .filter(a->a.getLease()!=null)
                .filter(a->a.getLease().getProperty()!=null)
                .filter(a->a.getLease().getProperty()==property)
                .collect(Collectors.toList());
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    ClockService clockService;
}
