package org.estatio.module.capex.integtests.order;

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

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.base.spiimpl.togglz.EstatioTogglzFeature;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.triggers.Order_discard;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.incoming.builders.CapexChargeHierarchyXlsxFixture;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class Order_withLinks_IntegTest extends CapexModuleIntegTestAbstract {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                ec.executeChild(this, new CapexChargeHierarchyXlsxFixture());
                ec.executeChildren(this,
                        Order_enum.fakeOrder2Pdf,
                        Budget_enum.OxfBudget2015,
                        Budget_enum.OxfBudget2016,
                        IncomingInvoice_enum.fakeInvoice2Pdf,
                        Person_enum.JonathanIncomingInvoiceManagerGb);
            }
        });
        order = Order_enum.fakeOrder2Pdf.findUsing(serviceRegistry);
        orderItem = order.getItems().first();

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

        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {
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
        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () ->
                wrap(orderItem).editPeriod("F2018")
        );
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
        final BigDecimal newNetAmt = fakeDataService.bigDecimals().any(13, 2).abs();
        final BigDecimal newVatAmt = fakeDataService.bigDecimals().any(13, 2).abs();
        final BigDecimal newGrossAmt = fakeDataService.bigDecimals().any(13, 2).abs();
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
