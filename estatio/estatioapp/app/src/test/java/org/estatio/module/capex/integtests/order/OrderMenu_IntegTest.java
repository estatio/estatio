package org.estatio.module.capex.integtests.order;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixtures.FixtureClock;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.HiddenException;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.capex.app.OrderMenu;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.transitions.Order_approvalTransitions;
import org.estatio.module.capex.dom.order.approval.triggers.Order_completeWithApproval;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.fixtures.order.enums.NumeratorForOrder_enum;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.capex.fixtures.charge.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderMenu_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject OrderMenu orderMenu;

    @Inject SudoService sudoService;

    @Inject QueryResultsCache queryResultsCache;

    public static class CreateOrder extends OrderMenu_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Organisation_enum.TopModelIt.builder());
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.RonIt.builder());
                    executionContext.executeChild(this, new IncomingChargesItaXlsxFixture());
                    executionContext.executeChild(this, Project_enum.RonProjectIt.builder());
                    executionContext.executeChild(this, Person_enum.CarmenIncomingInvoiceManagerIt.builder());
                    executionContext.executeChild(this, Person_enum.JonathanIncomingInvoiceManagerGb.builder());
                    executionContext.executeChild(this, NumeratorForOrder_enum.ItaScopedToHelloWorldIt.builder());
                }
            });
        }

        @Test
        public void createOrderForItaly_happyCase_with_property() throws Exception {
            // given
            final Property property = PropertyAndUnitsAndOwnerAndManager_enum.RonIt.getProperty_d().findUsing(serviceRegistry);
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItAcquisition.findUsing(serviceRegistry);
            final Organisation buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);
            final Numerator numerator = NumeratorForOrder_enum.ItaScopedToHelloWorldIt.findUsing(serviceRegistry);

            final Person incomingInvoiceManager = Person_enum.CarmenIncomingInvoiceManagerIt.findUsing(serviceRegistry);

            // when
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            final Order order = sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(orderMenu).createOrder(property, null, project, charge, buyer, null, orderMenu.default6CreateOrder(), null, null, null));

            // then
            assertThat(order.getOrderNumber()).isEqualTo("0001/RON/001/001");
            assertThat(order.getProperty()).isEqualTo(property);
            assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);
            assertThat(mixin(Order_approvalTransitions.class, order).coll()).hasSize(2);

            assertThat(order.getItems()).hasSize(1);
            final OrderItem item = order.getItems().first();

            assertThat(item.getProperty()).isEqualTo(property);
            assertThat(item.getProject()).isEqualTo(project);
            assertThat(item.getCharge()).isEqualTo(charge);

        }

        @Test
        public void createOrderForItaly_and_approve_happyCase_with_multi_property_ref() throws Exception {
            // given
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItAcquisition.findUsing(serviceRegistry);
            final Organisation buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);
            final Organisation supplier = Organisation_enum.TopModelIt.findUsing(serviceRegistry);
            final Numerator numerator = NumeratorForOrder_enum.ItaScopedToHelloWorldIt.findUsing(serviceRegistry);

            final Person incomingInvoiceManager = Person_enum.CarmenIncomingInvoiceManagerIt.findUsing(serviceRegistry);

            // when
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            final Order order = sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(orderMenu).createOrder(null, "GEN", project, charge, buyer, supplier, orderMenu.default6CreateOrder(), new BigDecimal("1000.00"), null, "some order description"));

            // then
            assertThat(order.getOrderNumber()).isEqualTo("0001/GEN/001/001");
            assertThat(order.getProperty()).isNull();
            assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);
            assertThat(order.getType()).isEqualTo(IncomingInvoiceType.ITA_ORDER_INVOICE);
            assertThat(mixin(Order_approvalTransitions.class, order).coll()).hasSize(2);

            assertThat(order.getItems()).hasSize(1);
            final OrderItem item = order.getItems().first();

            assertThat(item.getProperty()).isNull();
            assertThat(item.getProject()).isEqualTo(project);
            assertThat(item.getCharge()).isEqualTo(charge);

            // and when
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(mixin(Order_completeWithApproval.class, order)).act(incomingInvoiceManager, FixtureClock.getTimeAsLocalDate(), "approved"));

            // then
            assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.APPROVED);

        }

        @Test
        public void createOrderForItaly_sadCase_hidden_for_non_italians() throws Exception {
            // given
            final Property property = PropertyAndUnitsAndOwnerAndManager_enum.RonIt.getProperty_d().findUsing(serviceRegistry);
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItAcquisition.findUsing(serviceRegistry);
            final Organisation buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);

            final Person incomingInvoiceManager = Person_enum.JonathanIncomingInvoiceManagerGb.findUsing(serviceRegistry);

            // then
            expectedExceptions.expect(HiddenException.class);

            // when
            sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(orderMenu).createOrder(property, null, project, charge, buyer, null, orderMenu.default6CreateOrder(), null, null, null));
        }

        @Test
        public void createOrderForItaly_sadCase_both_property_and_ref() throws Exception {
            // given
            final Property property = PropertyAndUnitsAndOwnerAndManager_enum.RonIt.getProperty_d().findUsing(serviceRegistry);
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItAcquisition.findUsing(serviceRegistry);
            final Organisation buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);

            final Person incomingInvoiceManager = Person_enum.CarmenIncomingInvoiceManagerIt.findUsing(serviceRegistry);

            // then
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("Can not define both property and multi property reference");

            // when
            sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(orderMenu).createOrder(property, "GEN", project, charge, buyer, null, orderMenu.default6CreateOrder(), null, null, null));
        }

        @Test
        public void createOrderForItaly_sadCase_neither_property_or_ref() throws Exception {
            // given
            final Property property = PropertyAndUnitsAndOwnerAndManager_enum.RonIt.getProperty_d().findUsing(serviceRegistry);
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItAcquisition.findUsing(serviceRegistry);
            final Organisation buyer = Organisation_enum.HelloWorldIt.findUsing(serviceRegistry);

            final Person incomingInvoiceManager = Person_enum.CarmenIncomingInvoiceManagerIt.findUsing(serviceRegistry);

            // then
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("Either a property or a reference for multiple properties must be defined");

            // when
            sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(orderMenu).createOrder(null, null, project, charge, buyer, null, orderMenu.default6CreateOrder(), null, null, null));
        }

        @Test
        public void createOrderForItaly_sadCase_no_numerator() throws Exception {
            // given
            final Property property = PropertyAndUnitsAndOwnerAndManager_enum.RonIt.getProperty_d().findUsing(serviceRegistry);
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItAcquisition.findUsing(serviceRegistry);
            final Organisation buyer = Organisation_enum.TopModelIt.findUsing(serviceRegistry);

            final Person incomingInvoiceManager = Person_enum.CarmenIncomingInvoiceManagerIt.findUsing(serviceRegistry);

            // then
            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage("No order number numerator found for this buyer");

            // when
            final Order order = sudoService.sudo(incomingInvoiceManager.getUsername(), () ->
                    wrap(orderMenu).createOrder(property, null, project, charge, buyer, null, orderMenu.default6CreateOrder(), null, null, null));
        }
    }
}
