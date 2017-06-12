package org.estatio.integtests.agreement;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRepository;
import org.estatio.dom.agreement.role.AgreementRoleType;
import org.estatio.dom.agreement.role.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.lease.AgreementRoleTypeEnum;
import org.estatio.dom.lease.AgreementTypeEnum;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
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
        agreementType = agreementTypeRepository.find(AgreementTypeEnum.LEASE.getTitle());
        agreement = agreementRepository.findAgreementByTypeAndReference(agreementType, LeaseForOxfTopModel001Gb.REF);
        agreementRoleType = agreementRoleTypeRepository.findByAgreementTypeAndTitle(agreementType, AgreementRoleTypeEnum.TENANT.getTitle());

    }

    public static class FindByTitle extends AgreementRoleTypeRepository_IntegTest {

        @Test
        public void findByTitle() throws Exception {
            AgreementRoleType result = agreementRoleTypeRepository.findByTitle(AgreementRoleTypeEnum.TENANT.getTitle());
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
            AgreementRoleType result = agreementRoleTypeRepository.findByAgreementTypeAndTitle(agreementType, AgreementRoleTypeEnum.TENANT.getTitle());
            assertNotNull(result);
        }
    }
}
