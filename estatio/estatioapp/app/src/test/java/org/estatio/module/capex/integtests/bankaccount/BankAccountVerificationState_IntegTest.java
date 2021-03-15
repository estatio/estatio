package org.estatio.module.capex.integtests.bankaccount;

import javax.inject.Inject;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccount_verificationState;
import org.estatio.module.financial.dom.bankaccount.verification.triggers.BankAccount_discard;
import org.estatio.module.financial.dom.bankaccount.verification.triggers.BankAccount_proofUpdated;
import org.estatio.module.financial.dom.bankaccount.verification.triggers.BankAccount_rejectProof;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.task.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationState.NOT_VERIFIED;

public class BankAccountVerificationState_IntegTest extends CapexModuleIntegTestAbstract {

    Property propertyForViv;
    Party buyer;
    Party seller;

    Country france;
    Charge charge_for_works;

    IncomingInvoice incomingInvoice;

    BankAccount bankAccount;
    Project project;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                ec.executeChild(this, new IncomingChargesFraXlsxFixture());
                ec.executeChildren(this,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        BankAccount_enum.TopModelFr,
                        Person_enum.BrunoTreasurerFr,
                        Person_enum.BertrandIncomingInvoiceManagerFr,
                        Person_enum.KateCorporateAdministratorFr);
            }
        });
    }

    @Before
    public void setUp() {
        propertyForViv = Property_enum.VivFr.findUsing(serviceRegistry);

        buyer = OrganisationAndComms_enum.HelloWorldFr.findUsing(serviceRegistry);
        seller = OrganisationAndComms_enum.TopModelFr.findUsing(serviceRegistry);

        france = countryRepository.findCountry(Country_enum.FRA.getRef3());
        charge_for_works = chargeRepository.findByReference("WORKS");

        project = projectRepository.findByReference("OXF-02");

        bankAccount = BankAccount_enum.TopModelFr.findUsing(serviceRegistry);

        incomingInvoice = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate("65432", seller, new LocalDate(2014, 5, 13));
        incomingInvoice.setBankAccount(bankAccount);

        assertBankAccountState(bankAccount, NOT_VERIFIED);

    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Test
    public void corporate_invoice_update_proof_works() {

        // given
        Person personBrunoTreasurer = Person_enum.BrunoTreasurerFr.findUsing(serviceRegistry);
        PartyRoleType typeForTreasurer = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey());
        incomingInvoice.setType(IncomingInvoiceType.CORPORATE_EXPENSES);
        incomingInvoice.setApprovalState(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK);
        Person personKateCorporateAdmin = Person_enum.KateCorporateAdministratorFr.findUsing(serviceRegistry);
        PartyRoleType typeForCorporateAdministrator = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR.getKey());
        assertThat(taskRepository.findIncompleteByRole(typeForCorporateAdministrator).size()).isEqualTo(0);

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personBrunoTreasurer.getUsername(), (Runnable) () ->
                wrap(mixin(BankAccount_rejectProof.class, bankAccount)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, "NO GOOD"));

        // then
        assertBankAccountState(bankAccount, BankAccountVerificationState.AWAITING_PROOF);
        assertThat(taskRepository.findIncompleteByRole(typeForTreasurer).size()).isEqualTo(0);
        assertThat(taskRepository.findIncompleteByRole(typeForCorporateAdministrator).size()).isEqualTo(1);

        // and when 'resurrecting'
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personKateCorporateAdmin.getUsername(), (Runnable) () ->
                wrap(mixin(BankAccount_proofUpdated.class, bankAccount)).act(PartyRoleTypeEnum.TREASURER.findUsing(partyRoleTypeRepository), null, null));

        // then
        assertBankAccountState(bankAccount, BankAccountVerificationState.NOT_VERIFIED);
        assertThat(taskRepository.findIncompleteByRole(typeForTreasurer).size()).isEqualTo(1);
        assertThat(taskRepository.findIncompleteByRole(typeForCorporateAdministrator).size()).isEqualTo(0);
    }

    @Test
    public void reject_then_discard_then_update_proof_works_test() {

        // given
        Person personBrunoTreasurer = Person_enum.BrunoTreasurerFr.findUsing(serviceRegistry);
        Person personBertrandIncomingInvoiceManager = Person_enum.BertrandIncomingInvoiceManagerFr.findUsing(serviceRegistry);
        PartyRoleType typeForTreasurer = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.TREASURER.getKey());
        PartyRoleType typeForIncInvoiceManager = partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER.getKey());
        assertThat(taskRepository.findIncompleteByRole(typeForIncInvoiceManager).size()).isEqualTo(2);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personBertrandIncomingInvoiceManager.getUsername(), (Runnable) () ->
                wrap(mixin(IncomingInvoice_complete.class, incomingInvoice)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, null));

        // when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personBrunoTreasurer.getUsername(), (Runnable) () ->
                wrap(mixin(BankAccount_rejectProof.class, bankAccount)).act(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository), null, "NO GOOD"));

        // then
        assertBankAccountState(bankAccount, BankAccountVerificationState.AWAITING_PROOF);
        assertThat(taskRepository.findIncompleteByRole(typeForTreasurer).size()).isEqualTo(0);
        assertThat(taskRepository.findIncompleteByRole(typeForIncInvoiceManager).size()).isEqualTo(2);
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.COMPLETED);
        assertThat(taskRepository.findIncompleteByRole(typeForIncInvoiceManager).get(1).getDescription()).isEqualTo("Proof Updated (NO GOOD)");
        assertThat(taskRepository.findIncompleteByRole(typeForIncInvoiceManager).get(1).getPriority()).isEqualTo(1);

        // and when
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personBertrandIncomingInvoiceManager.getUsername(), (Runnable) () ->
                wrap(mixin(BankAccount_discard.class, bankAccount)).act("DISCARDING"));

        transactionService.nextTransaction();

        // then
        assertBankAccountState(bankAccount, BankAccountVerificationState.DISCARDED);
        assertThat(taskRepository.findIncompleteByRole(typeForTreasurer).size()).isEqualTo(0);
        assertThat(taskRepository.findIncompleteByRole(typeForIncInvoiceManager).size()).isEqualTo(2);
        assertThat(incomingInvoice.getApprovalState()).isEqualTo(IncomingInvoiceApprovalState.NEW);

        // and when 'resurrecting'
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personBertrandIncomingInvoiceManager.getUsername(), (Runnable) () ->
                wrap(mixin(BankAccount_proofUpdated.class, bankAccount)).act(PartyRoleTypeEnum.TREASURER.findUsing(partyRoleTypeRepository), null, null));

        // then
        assertBankAccountState(bankAccount, BankAccountVerificationState.NOT_VERIFIED);
        assertThat(taskRepository.findIncompleteByRole(typeForTreasurer).size()).isEqualTo(1);
        assertThat(taskRepository.findIncompleteByRole(typeForIncInvoiceManager).size()).isEqualTo(2);

        // and when discarding again by treasurer
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(personBrunoTreasurer.getUsername(), (Runnable) () ->
                wrap(mixin(BankAccount_discard.class, bankAccount)).act("DISCARDING"));

        assertBankAccountState(bankAccount, BankAccountVerificationState.DISCARDED);
        assertThat(taskRepository.findIncompleteByRole(typeForTreasurer).size()).isEqualTo(0);

    }

    void assertBankAccountState(final BankAccount bankAccount, final BankAccountVerificationState expected) {
        assertThat(wrap(mixin(BankAccount_verificationState.class, bankAccount)).prop()).isEqualTo(
                expected);
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    ProjectRepository projectRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    TaskRepository taskRepository;

}

