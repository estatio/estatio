package org.estatio.integtests.agreement;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseAgreementTypeEnum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.lease.fixtures.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementRoleTypeRepository_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartyRepository partyRepository;

    @Inject
    AgreementRepository agreementRepository;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    Party party;
    Agreement agreement;
    AgreementType agreementType;
    AgreementRoleType agreementRoleType;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            }
        });
    }

    @Before
    public void setUp() throws Exception {
        party = partyRepository.findPartyByReference(LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
        agreementType = agreementTypeRepository.find(LeaseAgreementTypeEnum.LEASE.getTitle());
        agreement = agreementRepository.findAgreementByTypeAndReference(agreementType, LeaseForOxfTopModel001Gb.REF);
        agreementRoleType = agreementRoleTypeRepository.findByAgreementTypeAndTitle(agreementType, LeaseAgreementRoleTypeEnum.TENANT.getTitle());

    }

    public static class FindByTitle extends AgreementRoleTypeRepository_IntegTest {

        @Test
        public void findByTitle() throws Exception {
            AgreementRoleType result = agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
            assertThat(result, is(agreementRoleType));
        }
    }

    public static class FindApplicableTo extends AgreementRoleTypeRepository_IntegTest {

        @Test
        public void findApplicableTo() throws Exception {
            List<AgreementRoleType> result = agreementRoleTypeRepository.findApplicableTo(agreementType);
            assertThat(result.size(), is(agreementType.getRoleTypesApplicableTo().size()));
            assertThat(result, is(agreementType.getRoleTypesApplicableTo()));
        }
    }

    public static class FindByAgreementTypeAndTitle extends AgreementRoleTypeRepository_IntegTest {

        @Test
        public void findByAgreementTypeAndTitle() throws Exception {
            AgreementRoleType result = agreementRoleTypeRepository.findByAgreementTypeAndTitle(agreementType, LeaseAgreementRoleTypeEnum.TENANT.getTitle());
            assertNotNull(result);
        }
    }
}
