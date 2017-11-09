package org.estatio.integtests.agreement;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseAgreementTypeEnum;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.application.fixtures.EstatioBaseLineFixture;
import org.estatio.module.application.fixtures.lease.LeaseForKalPoison001Nl;
import org.estatio.module.application.fixtures.person.personas.PersonAndRolesForJohnDoeNl;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class Agreement_IntegTest extends EstatioIntegrationTest {

    @Inject
    AgreementRepository agreementRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    Agreement agreement;

    Party party;

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new LeaseForKalPoison001Nl());
            }
        });
    }

    @Before
    public void setUp() {
        agreement = agreementRepository.findAgreementByTypeAndReference(agreementTypeRepository.find(
                LeaseAgreementTypeEnum.LEASE.getTitle()), LeaseForKalPoison001Nl.REF);
        assertNotNull(agreement);

        party = partyRepository.findPartyByReference(PersonAndRolesForJohnDoeNl.REF);
        assertThat(party.getReference(), is(PersonAndRolesForJohnDoeNl.REF));
    }

    public static class NewRole extends Agreement_IntegTest {

        @Test
        public void happyCase() throws Exception {
            // given
            AgreementRole existingRole = agreementRoleRepository.findByParty(party).get(0);

            // when
            existingRole.setEndDate(new LocalDate(2013, 12, 31));
            wrap(agreement).newRole(agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.MANAGER.getTitle()), party, new LocalDate(2014, 1, 1), new LocalDate(2014, 12, 31));
            wrap(agreement).newRole(agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.MANAGER.getTitle()), party, new LocalDate(2015, 1, 1), null);

            // then
            assertThat(agreementRoleRepository.findByParty(party).size(), is(3));
        }

        @Test(expected = InvalidException.class)
        public void sadCase() throws Exception {
            // given
            assertThat(agreementRoleRepository.findByParty(party).size(), is(1));

            // when
            wrap(agreement).newRole(agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.MANAGER.getTitle()), party, null, new LocalDate(2014, 12, 31));
            wrap(agreement).newRole(agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.MANAGER.getTitle()), party, new LocalDate(2014, 12, 31), null);

        }
    }
}
