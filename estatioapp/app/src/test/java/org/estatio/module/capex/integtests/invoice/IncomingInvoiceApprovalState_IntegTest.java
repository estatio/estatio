package org.estatio.module.capex.integtests.invoice;

import java.util.SortedSet;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.person.personas.PersonAndRolesForEmmaTreasurerGb;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.personas.BankAccountAndFaFaForTopModelGb;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccount_verificationState;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.IncomingInvoiceFixture;
import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.country.fixtures.enums.Country_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForTopModelGb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState.NOT_VERIFIED;

public class IncomingInvoiceApprovalState_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForOxf;
    Party buyer;
    Party seller;

    Country greatBritain;
    Charge charge_for_works;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;
    Project project;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                executionContext.executeChild(this, new IncomingChargeFixture());
                executionContext.executeChild(this, new IncomingInvoiceFixture());
                executionContext.executeChild(this, new BankAccountAndFaFaForTopModelGb());
                executionContext.executeChild(this, new PersonAndRolesForEmmaTreasurerGb());
            }
        });

//        TickingFixtureClock.replaceExisting();
    }

    @Before
    public void setUp() {
        propertyForOxf = propertyRepository.findPropertyByReference(PropertyAndUnitsAndOwnerAndManagerForOxfGb.REF);

        buyer = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
        seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);

        greatBritain = countryRepository.findCountry(Country_enum.GBR.getRef3());
        charge_for_works = chargeRepository.findByReference("WORKS");

        project = projectRepository.findByReference("OXF-02");

        bankAccount = bankAccountRepository.findBankAccountByReference(seller, BankAccountAndFaFaForTopModelGb.REF);

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("65432", seller, new LocalDate(2014,5,13));
        incomingInvoice.setBankAccount(bankAccount);

        assertThat(incomingInvoice).isNotNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);
        assertState(bankAccount, NOT_VERIFIED);

    }

//    @After
//    public void tearDown() {
//        TickingFixtureClock.reinstateExisting();
//    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Test
    public void complete_should_fail_when_not_having_appropriate_role_type_test() {

        Exception error = new Exception();

        // given
        Person personEmmaWithNoRoleAsPropertyManager = (Person) partyRepository.findPartyByReference(
                PersonAndRolesForEmmaTreasurerGb.REF);
        SortedSet<PartyRole> rolesforEmma = personEmmaWithNoRoleAsPropertyManager.getRoles();
        assertThat(rolesforEmma.size()).isEqualTo(1);
        assertThat(rolesforEmma.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey()));

        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(PersonAndRolesForEmmaTreasurerGb.REF.toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNotNull();
        assertThat(error.getMessage()).contains("Reason: Task assigned to 'PROPERTY_MANAGER' role");

    }

    @Test
    public void complete_works_when_having_appropriate_role_type_test() {

        Exception error = new Exception();

        // given
        Person personEmma = (Person) partyRepository.findPartyByReference(PersonAndRolesForEmmaTreasurerGb.REF);
        PartyRoleType roleAsPropertyManager = partyRoleTypeRepository.findByKey("PROPERTY_MANAGER");
        personEmma.addRole(roleAsPropertyManager);
        transactionService.nextTransaction();
        SortedSet<PartyRole> rolesforEmma = personEmma.getRoles();
        assertThat(rolesforEmma.size()).isEqualTo(2);
        assertThat(rolesforEmma.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey("PROPERTY_MANAGER"));

        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(PersonAndRolesForEmmaTreasurerGb.REF.toLowerCase(), (Runnable) () ->
                    wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act("PROPERTY_MANAGER", null, null));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNull();
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);

    }

    void assertState(final BankAccount bankAccount, final BankAccountVerificationState expected) {
        Assertions.assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
                expected);
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}

