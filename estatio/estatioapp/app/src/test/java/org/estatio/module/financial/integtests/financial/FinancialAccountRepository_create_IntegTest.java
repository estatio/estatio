package org.estatio.module.financial.integtests.financial;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountType;
import org.estatio.module.financial.integtests.FinancialModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class FinancialAccountRepository_create_IntegTest extends FinancialModuleIntegTestAbstract {


    @Before
    public void setUp() throws Exception {
        runFixtureScript(
                OrganisationAndComms_enum.HelloWorldGb.builder()
        );
        this.party = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
    }

    private Party party;

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Test
    public void when_bankAccount() throws Exception {

        final FinancialAccountType[] financialAccountTypes = FinancialAccountType.values();
        for (final FinancialAccountType financialAccountType : financialAccountTypes) {

            String someRef = fakeDataService.strings().digits(4);
            String someName = fakeDataService.strings().upper(20);

            // given
            final List<FinancialAccount> before = financialAccountRepository.allAccounts();

            // when
            final FinancialAccount financialAccount = financialAccountRepository
                    .newFinancialAccount(financialAccountType, someRef, someName, party);


            // then
            assertThat(financialAccount.getType()).isEqualTo(financialAccountType);
            assertThat(financialAccount.getReference()).isEqualTo(someRef);
            assertThat(financialAccount.getName()).isEqualTo(someName);

            final List<FinancialAccount> after = financialAccountRepository.allAccounts();
            assertThat(after).contains(financialAccount);
            assertThat(after.size()).isEqualTo(before.size()+1);

        }

    }

    @Inject
    FakeDataService fakeDataService;

}
