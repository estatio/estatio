package org.estatio.integtests.agreement;

import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementRoleTypesTest extends EstatioIntegrationTest {

    @Inject
    Parties parties;

    @Inject
    Agreements agreements;

    @Inject
    AgreementTypes agreementTypes;

    @Inject
    AgreementRoleTypes agreementRoleTypes;

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
                executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
            }
        });
    }

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(_LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
        agreement = agreements.findAgreementByReference(_LeaseForOxfTopModel001Gb.REF);
        agreementType = agreementTypes.find(LeaseConstants.AT_LEASE);
        agreementRoleType = agreementRoleTypes.findByAgreementTypeAndTitle(agreementType, LeaseConstants.ART_TENANT);

    }

    public static class FindByTitle extends AgreementRoleTypesTest {

        @Test
        public void findByTitle() throws Exception {
            AgreementRoleType result = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
            assertThat(result, is(agreementRoleType));
        }
    }

    public static class FindApplicableTo extends AgreementRoleTypesTest {

        @Test
        public void findApplicableTo() throws Exception {
            List<AgreementRoleType> result = agreementRoleTypes.findApplicableTo(agreementType);
            assertThat(result.size(), is(agreementType.getRoleTypesApplicableTo().size()));
            assertThat(result, is(agreementType.getRoleTypesApplicableTo()));
        }
    }

    public static class FindByAgreementTypeAndTitle extends AgreementRoleTypesTest {

        @Test
        public void findByAgreementTypeAndTitle() throws Exception {
            AgreementRoleType result = agreementRoleTypes.findByAgreementTypeAndTitle(agreementType, LeaseConstants.ART_TENANT);
            assertNotNull(result);
        }
    }
}
