package org.estatio.module.capex.integtests.order;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.HiddenException;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.transitions.Document_categorisationTransitions;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.triggers.Order_amend;
import org.estatio.module.capex.dom.order.approval.triggers.Order_completeWithApproval;
import org.estatio.module.capex.dom.order.approval.triggers.Order_discard;
import org.estatio.module.capex.fixtures.OrderFixture;
import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class Order_2_IntegTest extends CapexModuleIntegTestAbstract {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Before
    public void setupData() {

        final OrderFixture orderFixture = new OrderFixture();
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {

                // taken from the DocumentTypesAndTemplatesSeedService (not run in integ tests by default)
                final LocalDate templateDate = new LocalDate(2012,1,1);
                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture(templateDate));

                executionContext.executeChild(this, new IncomingChargeFixture());
                executionContext.executeChild(this, orderFixture);
                executionContext.executeChild(this, Person_enum.JonathanPropertyManagerGb.toFixtureScript());
            }
        });
        order = orderFixture.getOrder();
    }

    Order order;


    @Test
    public void orderFixtureLoadedProperly() throws Exception {

        assertThat(orderRepository.listAll().size()).isEqualTo(1);

        List<Document> unclassifiedIncomingOrders = incomingDocumentRepository.findUnclassifiedIncomingOrders();
        assertThat(unclassifiedIncomingOrders.size()).isEqualTo(0);

        Document fakeOrderDoc = incomingDocumentRepository.findAllIncomingDocumentsByName("fakeOrder2.pdf").get(0);
        List<IncomingDocumentCategorisationStateTransition> transitions = mixin(Document_categorisationTransitions.class, fakeOrderDoc).coll();
        assertThat(transitions.size()).isEqualTo(2);
    }

    @Test
    public void can_approve() throws Exception {

        // given
        assertNotNull(order);

        // when
        approve(Person_enum.JonathanPropertyManagerGb.getSecurityUserName(), this.order);

        // then
        assertThat(this.order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);

    }

    @Test
    public void can_discard_when_still_new() throws Exception {

        // given
        assertNotNull(order);
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);

        // when
        final Order_discard mixin = mixin(Order_discard.class, order);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act("Discarding junk");
        });

        // then
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.DISCARDED);


    }

    @Test
    public void cannot_discard_when_has_been_approved() throws Exception {

        // given
        assertNotNull(order);
        approve(Person_enum.JonathanPropertyManagerGb.getSecurityUserName(), order);
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);

        // expect
        expectedExceptions.expect(HiddenException.class);

        // when attempt
        final Order_discard mixin = mixin(Order_discard.class, order);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act("Discarding junk");
        });

    }

    @Test
    public void can_amend_when_approved() throws Exception {

        // given
        assertNotNull(order);
        approve(Person_enum.JonathanPropertyManagerGb.getSecurityUserName(), order);
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);

        // when
        final Order_amend mixin = mixin(Order_amend.class, order);

        final String role = mixin.default0Act();
        final Person person = mixin.default1Act();
        final String comment = "some reason";

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanPropertyManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act(role, person, comment);
        });

        // then
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);
    }


    private void approve(final String username, final Order order) {
        final Order_completeWithApproval mixin = mixin(Order_completeWithApproval.class, order);

        final Person approvedBy = personRepository.autoComplete("***").get(0);
        final LocalDate approvedOn = clockService.now();
        final String comment = "some comment";

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(username, () -> {
            wrap(mixin).act(approvedBy, approvedOn, comment);
        });
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    ClockService clockService;

    @Inject
    OrderRepository orderRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

}
