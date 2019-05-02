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
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.HiddenException;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

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
import org.estatio.module.capex.dom.order.recategorize.Order_recategorize;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.incode.module.base.integtests.VT.ld;
import static org.junit.Assert.assertNotNull;

public class Order_2_IntegTest extends CapexModuleIntegTestAbstract {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {

                // taken from the DocumentTypesAndTemplatesSeedService (not run in integ tests by default)
                final LocalDate templateDate = ld(2012,1,1);

                executionContext.executeChildren(this,
                        new DocumentTypesAndTemplatesForCapexFixture(templateDate),
                        new IncomingChargesFraXlsxFixture());

                executionContext.executeChildren(this,
                        Order_enum.fakeOrder2Pdf,
                        Person_enum.JonathanIncomingInvoiceManagerGb);
            }
        });
        order = Order_enum.fakeOrder2Pdf.findUsing(serviceRegistry);
    }

    Order order;


    @Test
    public void orderFixtureLoadedProperly() throws Exception {

        assertThat(orderRepository.listAll().size()).isEqualTo(1);

        List<Document> unclassifiedIncomingOrders = incomingDocumentRepository.findUnclassifiedIncomingOrders();
        assertThat(unclassifiedIncomingOrders.size()).isEqualTo(0);

        final String documentName = Order_enum.fakeOrder2Pdf.getDocument_d().findUsing(serviceRegistry).getName();
        Document document = incomingDocumentRepository.findAllIncomingDocumentsByName(documentName).get(0);
        List<IncomingDocumentCategorisationStateTransition> transitions =
                mixin(Document_categorisationTransitions.class, document).coll();
        assertThat(transitions.size()).isEqualTo(2);
    }

    @Test
    public void can_approve() throws Exception {

        // given
        assertNotNull(order);

        // when
        approve(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), this.order);

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
        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act("Discarding junk");
        });

        // then
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.DISCARDED);


    }

    @Test
    public void cannot_discard_when_has_been_approved() throws Exception {

        // given
        assertNotNull(order);
        approve(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), order);
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);

        // expect
        expectedExceptions.expect(HiddenException.class);

        // when attempt
        final Order_discard mixin = mixin(Order_discard.class, order);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act("Discarding junk");
        });

    }

    @Test
    public void can_amend_when_approved() throws Exception {

        // given
        assertNotNull(order);
        approve(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), order);
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);

        // when
        final Order_amend mixin = mixin(Order_amend.class, order);

        final IPartyRoleType role = mixin.default0Act();
        final Person person = mixin.default1Act(role);
        final String comment = "some reason";

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act(role, person, comment);
        });

        // then
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);
    }

    @Test
    public void can_recategorize() {

        // given and unapproved order
        assertNotNull(order);
        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);
        final String orderNumber = order.getOrderNumber();

        // .. and given the order has an attached document
        final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(order);
        assertThat(paperclips).hasSize(1);
        final DocumentAbstract doc = paperclips.get(0).getDocument();

        // .. and given there are no other incoming documents
        final List<Document> incomingDocumentsBefore =
                incomingDocumentRepository.findIncomingDocuments();
        assertThat(incomingDocumentsBefore).hasSize(0);


        // when
        final Order_recategorize mixin = mixin(Order_recategorize.class, order);

        final String comment = "some reason";

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act(comment);
        });
        queryResultsCache.resetForNextTransaction();

        // then the order is deleted
        final Order order = orderRepository.findByOrderNumber(orderNumber);
        assertThat(order).isNull();

        // .. and then the document that was attached to the order is now back to being an incoming document
        final List<Document> incomingDocumentsAfter = incomingDocumentRepository.findIncomingDocuments();
        assertThat(incomingDocumentsAfter).hasSize(1);
        assertThat(incomingDocumentsAfter.get(0)).isEqualTo(doc);
    }

    @Test
    public void cannot_recategorize_if_not_new() {

        // given
        assertNotNull(order);
        approve(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), order);
        assertThat(order.getApprovalState()).isNotEqualTo(OrderApprovalState.NEW);

        // expect
        expectedExceptions.expect(DisabledException.class);
        expectedExceptions.expectMessage("Only NEW orders can be recategorized");

        // when
        final Order_recategorize mixin = mixin(Order_recategorize.class, order);

        final String comment = "some reason";

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {
            wrap(mixin).act(comment);
        });
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
    PaperclipRepository paperclipRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;


}
