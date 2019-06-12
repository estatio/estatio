package org.estatio.module.lease.integtests.agreement;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertNotNull;

public class Agreement_primaryParty_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    ClockService clockService;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    @Inject
    ServiceRegistry2 serviceRegistry;

    Agreement agreement;

    Party landlordParty;
    Party newLandlordParty;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {
                ec.executeChild(this, Lease_enum.KalPoison001Nl);
                ec.executeChild(this, Organisation_enum.DagoBankNl);
            }
        });
    }

    @Before
    public void setUp() {
        agreement = Lease_enum.KalPoison001Nl.findUsing(serviceRegistry);
        assertNotNull(agreement);

        landlordParty = Organisation_enum.AcmeNl.findUsing(serviceRegistry);
        assertNotNull(landlordParty);

        newLandlordParty = Organisation_enum.DagoBankNl.findUsing(serviceRegistry);
        assertNotNull(newLandlordParty);
    }

    @Test
    public void happyCase() throws Exception {

        // given
        final AgreementRoleType landlordRoleType =
                agreementRoleTypeRepository.find(LeaseAgreementRoleTypeEnum.LANDLORD);

        final List<AgreementRole> landlordRoles = agreementRoleRepository.findByAgreement(agreement)
                .stream()
                .filter(x -> x.getType() == landlordRoleType)
                .collect(Collectors.toList());
        assertThat(landlordRoles).hasSize(1);

        final AgreementRole landlordRole = landlordRoles.stream().findFirst().orElse(null);
        assertThat(landlordRole).isNotNull();
        assertThat(landlordRole.getParty()).isSameAs(landlordParty);
        assertThat(landlordRole.getAgreement()).isSameAs(agreement);
        assertThat(landlordRole.getStartDate()).isNull();
        assertThat(landlordRole.getEndDate()).isNull();

        // when, then
        final Party landlord = agreement.getPrimaryParty();
        assertThat(landlord).isSameAs(landlordParty);

        // when, then
        final Party primaryPartyNow = agreement.primaryPartyAsOfElseCurrent(clockService.now());
        assertThat(primaryPartyNow).isSameAs(landlordParty);

        // when we look to the future, the primary party is unchanged
        final LocalDate tenDaysFromNow = clockService.now().plusDays(10);
        final Party primaryPartyTenDaysFromNow = agreement.primaryPartyAsOfElseCurrent(tenDaysFromNow);
        assertThat(primaryPartyTenDaysFromNow).isSameAs(landlordParty);

        // but given we set a new party for the future
        wrap(landlordRole).succeededBy(newLandlordParty, tenDaysFromNow, null);
        final List<AgreementRole> landlordRoles2 = agreementRoleRepository.findByAgreement(agreement)
                .stream()
                .filter(x -> x.getType() == landlordRoleType)
                .collect(Collectors.toList());
        assertThat(landlordRoles2).hasSize(2);

        // when we look at the primary party now, still unchanged
        final Party primaryPartyNow2 = agreement.primaryPartyAsOfElseCurrent(clockService.now());
        assertThat(primaryPartyNow2).isSameAs(landlordParty);

        // but when we look to the future, it is changed.
        final Party primaryPartyTenDaysFromNow2 = agreement.primaryPartyAsOfElseCurrent(tenDaysFromNow);
        assertThat(primaryPartyTenDaysFromNow2).isSameAs(newLandlordParty);
    }

}
