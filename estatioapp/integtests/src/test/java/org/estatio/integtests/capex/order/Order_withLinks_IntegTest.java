package org.estatio.integtests.capex.order;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.triggers.Order_discard;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.dom.togglz.EstatioTogglzFeature;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.budget.BudgetsForOxf;
import org.estatio.fixture.invoice.IncomingInvoiceFixture;
import org.estatio.fixture.order.OrderFixture;
import org.estatio.fixture.party.PersonForJonathanPropertyManagerGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class Order_withLinks_IntegTest extends EstatioIntegrationTest {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Before
    public void setupData() {

        final OrderFixture orderFixture = new OrderFixture();
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, orderFixture);
                executionContext.executeChild(this, new BudgetsForOxf());
                executionContext.executeChild(this, new IncomingInvoiceFixture());
                executionContext.executeChild(this, new PersonForJonathanPropertyManagerGb());
            }
        });
        order = orderFixture.getOrder();
        orderItem = orderFixture.getFirstItem();

        // given
        assertNotNull(order);
        assertNotNull(orderItem);
        assertThat(linkRepository.findByOrderItem(orderItem)).isNotEmpty();

        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);

        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache

    }

    Order order;
    OrderItem orderItem;


    @Test
    public void cannot_discard_when_has_linked_items() throws Exception {

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        final Order_discard mixin = mixin(Order_discard.class, order);

        sudoService.sudo(PersonForJonathanPropertyManagerGb.SECURITY_USERNAME, () -> {
            wrap(mixin).act("Discarding junk");
        });

    }

    @Test
    public void cannot_update_charge_dimension_when_has_linked_items() throws Exception {

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        wrap(orderItem).editCharge(fakeDataService.collections().anyOf(chargeRepository.allIncoming()));
    }

    @Test
    public void cannot_update_budget_dimension_when_has_linked_items() throws Exception {

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        final List<BudgetItem> list = budgetItemRepository.allBudgetItems();

        assertThat(list).isNotEmpty();
        wrap(orderItem).editBudgetItem(fakeDataService.collections().anyOf(list));

    }

    @Test
    public void cannot_update_project_dimension_when_has_linked_items() throws Exception {

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        wrap(orderItem).editProject(fakeDataService.collections().anyOf(projectRepository.listAll()));

    }

    @Test
    public void cannot_update_property_dimension_when_has_linked_items() throws Exception {

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        wrap(orderItem).editProperty(fakeDataService.collections().anyOf(propertyRepository.allProperties()));

    }

    @Test
    public void cannot_update_period_dimension_when_has_linked_items() throws Exception {

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        wrap(orderItem).editPeriod("F2018");
    }

    @Test
    public void can_update_description_even_when_has_linked_items() throws Exception {

        // when
        final String newDesc = fakeDataService.lorem().sentence();
        wrap(orderItem).editDescription(newDesc);

        // then
        assertThat(orderItem.getDescription()).isEqualTo(newDesc);
    }

    @Test
    public void can_update_amounts_even_when_has_linked_items() throws Exception {

        // when
        final BigDecimal newNetAmt = fakeDataService.bigDecimals().any(13,2).abs();
        final BigDecimal newVatAmt = fakeDataService.bigDecimals().any(13,2).abs();
        final BigDecimal newGrossAmt = fakeDataService.bigDecimals().any(13,2).abs();
        final Tax tax = fakeDataService.collections().anyOf(taxRepository.allTaxes());

        wrap(orderItem).updateAmounts(newNetAmt, newVatAmt, newGrossAmt, tax);

        // then
        assertThat(orderItem.getNetAmount()).isEqualTo(newNetAmt);
        assertThat(orderItem.getVatAmount()).isEqualTo(newVatAmt);
        assertThat(orderItem.getGrossAmount()).isEqualTo(newGrossAmt);
        assertThat(orderItem.getTax()).isEqualTo(tax);
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

    @Inject
    OrderItemInvoiceItemLinkRepository linkRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    BudgetItemRepository budgetItemRepository;

    @Inject TaxRepository taxRepository;

    @Inject ProjectRepository projectRepository;

    @Inject PropertyRepository propertyRepository;

    @Inject
    FakeDataService fakeDataService;


}
