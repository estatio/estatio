package org.estatio.module.coda.dom.doc;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budget.Status;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;

import static org.assertj.core.api.Assertions.assertThat;

public class DerivedObjectUpdater_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    StateTransitionService mockStateTransitionService;
    @Mock
    DerivedObjectLookup mockDerivedObjectLookup;

    CodaDocHead codaDocHead;
    IncomingInvoice incomingInvoice;
    DerivedObjectUpdater derivedObjectUpdater;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead();
        incomingInvoice = new IncomingInvoice();
        derivedObjectUpdater = new DerivedObjectUpdater();
        derivedObjectUpdater.stateTransitionService = mockStateTransitionService;
        derivedObjectUpdater.derivedObjectLookup = mockDerivedObjectLookup;
    }

    @Test
    public void tryUpdatePendingTaskIfRequired_when_invoice_is_paid_but_has_errors() {
        // given
        ErrorSet errorSet = new ErrorSet();
        errorSet.add("We added new validation and this existing invoice is now invalid!");

        // expecting
        context.checking(new Expectations() {{
            oneOf(mockDerivedObjectLookup).invoiceIfAnyFrom(codaDocHead);
            will(returnValue(incomingInvoice));
            oneOf(mockStateTransitionService).pendingTransitionOf(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
            will(returnValue(null));
        }});

        // when
        derivedObjectUpdater.tryUpdatePendingTaskIfRequired(codaDocHead, errorSet);
    }

    @Test
    public void tryUpdatePendingTaskIfRequired_when_invoice_is_in_state_complete() {
        // given
        ErrorSet errorSet = new ErrorSet();
        errorSet.add("We added new validation and this existing invoice is now invalid!");

        Task task = new Task(null, null, "I am valid!", null, null, null);
        IncomingInvoiceApprovalStateTransition transition = new IncomingInvoiceApprovalStateTransition();
        transition.setTransitionType(IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        transition.setTask(task);

        assertThat(task.getDescription()).isEqualTo("I am valid!");

        // expecting
        context.checking(new Expectations() {{
            oneOf(mockDerivedObjectLookup).invoiceIfAnyFrom(codaDocHead);
            will(returnValue(incomingInvoice));
            oneOf(mockStateTransitionService).pendingTransitionOf(incomingInvoice, IncomingInvoiceApprovalStateTransition.class);
            will(returnValue(transition));
        }});

        // when
        derivedObjectUpdater.tryUpdatePendingTaskIfRequired(codaDocHead, errorSet);

        // then
        assertThat(task.getDescription()).isEqualTo("We added new validation and this existing invoice is now invalid!");
    }

    @Mock BudgetRepository mockBudgetRepository;

    @Test
    public void deriveBudgetItem_when_no_budget_found() throws Exception {

        derivedObjectUpdater.budgetRepository = mockBudgetRepository;

        final Property property = new Property();
        final LocalDate invoiceDate = new LocalDate(2000, 01, 15);
        final Charge charge = new Charge();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetRepository).findByPropertyAndDate(property, invoiceDate);
            will(returnValue(null));
        }});

        // when
        final BudgetItem budgetItem = derivedObjectUpdater.deriveBudgetItem(property, invoiceDate, charge);

        // then
        assertThat(budgetItem).isNull();

    }

    @Test
    public void deriveBudgetItem_when_status_not_assigned_found() throws Exception {

        derivedObjectUpdater.budgetRepository = mockBudgetRepository;

        final Property property = new Property();
        final LocalDate invoiceDate = new LocalDate(2000, 01, 15);
        final Charge charge = new Charge();
        final Budget budget = new Budget();
        budget.setStatus(Status.NEW);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetRepository).findByPropertyAndDate(property, invoiceDate);
            will(returnValue(budget));
        }});

        // when
        final BudgetItem budgetItem = derivedObjectUpdater.deriveBudgetItem(property, invoiceDate, charge);

        // then
        assertThat(budgetItem).isNull();

    }

    @Test
    public void deriveBudgetItem_when_no_budgetItems_found() throws Exception {

        derivedObjectUpdater.budgetRepository = mockBudgetRepository;

        final Property property = new Property();
        final LocalDate invoiceDate = new LocalDate(2000, 01, 15);
        final Charge charge = new Charge();

        final Budget budget = new Budget();
        budget.setStatus(Status.ASSIGNED);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetRepository).findByPropertyAndDate(property, invoiceDate);
            will(returnValue(budget));
        }});

        // when
        final BudgetItem budgetItem = derivedObjectUpdater.deriveBudgetItem(property, invoiceDate, charge);

        // then
        assertThat(budgetItem).isNull();

    }

    @Test
    public void deriveBudgetItem_works_found() throws Exception {

        derivedObjectUpdater.budgetRepository = mockBudgetRepository;

        final Property property = new Property();
        final LocalDate invoiceDate = new LocalDate(2000, 01, 15);
        final Charge charge = new Charge();

        final Budget budget = new Budget();
        budget.setStatus(Status.ASSIGNED);
        final BudgetItem budgetItem = new BudgetItem();
        budgetItem.setCharge(charge);
        budget.getItems().add(budgetItem);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockBudgetRepository).findByPropertyAndDate(property, invoiceDate);
            will(returnValue(budget));
        }});

        // when
        final BudgetItem result = derivedObjectUpdater.deriveBudgetItem(property, invoiceDate, charge);

        // then
        assertThat(result).isEqualTo(budgetItem);

    }


}