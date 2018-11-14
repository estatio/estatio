package org.estatio.module.capex.integtests.order;

import java.util.SortedSet;

import javax.inject.Inject;

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
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.triggers.Order_completeWithApproval;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderApprovalState_IntegTest extends CapexModuleIntegTestAbstract {

    Party buyer;
    Party seller;

    Country france;
    Charge charge_for_works;

    Order order;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                ec.executeChild(this, new IncomingChargesFraXlsxFixture());
                ec.executeChildren(this,
                        Order_enum.fakeOrder3Pdf,
                        Person_enum.DanielOfficeAdministratorFr,
                        Person_enum.BertrandIncomingInvoiceManagerFr);
            }
        });

    }

    @Before
    public void setUp() {

        buyer = OrganisationAndComms_enum.HelloWorldFr.findUsing(serviceRegistry);
        seller = OrganisationAndComms_enum.TopModelFr.findUsing(serviceRegistry);

        france = countryRepository.findCountry(Country_enum.FRA.getRef3());
        charge_for_works = chargeRepository.findByReference("WORKS");

        order = Order_enum.fakeOrder3Pdf.findUsing(serviceRegistry);

        assertThat(order).isNotNull();
        assertThat(order.getProperty()).isNull();
        assertThat(taskRepository.findIncompleteByRole(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR.getKey())).size()).isEqualTo(1);

    }

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Test
    public void complete_order_of_type_local_expenses_should_fail_when_not_having_office_administrator_role_test() {

        Exception error = new Exception();

        // given
        Person personOlive = (Person) partyRepository.findPartyByReference(
                Person_enum.BertrandIncomingInvoiceManagerFr.getRef());
        SortedSet<PartyRole> rolesforJonathan = personOlive.getRoles();
        assertThat(rolesforJonathan.size()).isEqualTo(1);
        assertThat(rolesforJonathan.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER.getKey()));


        // when
        try {
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                    wrap(mixin(Order_completeWithApproval.class, order)).act( personOlive, new LocalDate(2018,1, 6), null));
        } catch (DisabledException e){
            error = e;
        }

        assertThat(error.getMessage()).isNotNull();
        assertThat(error.getMessage()).contains("Reason: Task assigned to 'OFFICE_ADMINISTRATOR' role");

    }

    @Test
    public void complete_order_of_type_local_expenses_works_when_having_office_administrator_role_test() {

        // given
        Person personDaniel = (Person) partyRepository.findPartyByReference(
                Person_enum.DanielOfficeAdministratorFr.getRef());
        SortedSet<PartyRole> rolesforDylan = personDaniel.getRoles();
        assertThat(rolesforDylan.size()).isEqualTo(1);
        assertThat(rolesforDylan.first().getRoleType()).isEqualTo(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR.getKey()));

        // when

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        setFixtureClockDate(2018,1, 6);
        sudoService.sudo(Person_enum.DanielOfficeAdministratorFr.getRef().toLowerCase(), (Runnable) () ->
                wrap(mixin(Order_completeWithApproval.class, order)).act( personDaniel, new LocalDate(2018,1, 6), null));
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);
        assertThat(taskRepository.findIncompleteByRole(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR.getKey())).size()).isEqualTo(0);

    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    PartyRepository partyRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    TaskRepository taskRepository;

}

