package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceItem_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;


    public static class RemoveItem_Test extends IncomingInvoiceItem_Test {

        @Test
        public void removeItem() throws Exception {

            // given
            IncomingInvoiceItem item = new IncomingInvoiceItem(){
                @Override
                boolean isLinkedToOrderItem(){
                    return false;
                }
            };
            item.repositoryService = mockRepositoryService;

            // expect
            context.checking(new Expectations(){
                {
                    oneOf(mockRepositoryService).removeAndFlush(item);
                }
            });

            // when
            item.removeItem();

        }

    }

    public static class SubtractAmounts_Test extends IncomingInvoiceItem_Test {

        @Test
        public void subtractAmounts_works() throws Exception {

            IncomingInvoiceItem item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());

            // given
            BigDecimal amount = new BigDecimal("100.00");
            item.setNetAmount(amount);
            item.setVatAmount(null);
            item.setGrossAmount(amount);

            // when
            BigDecimal netToSubtract = new BigDecimal("50.50");
            BigDecimal vatToSubtract = new BigDecimal("10.10");
            BigDecimal grossToSubtract = null;
            item.subtractAmounts(netToSubtract, vatToSubtract, grossToSubtract);

            // then
            assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("49.50"));
            assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("-10.10"));
            assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));

        }

    }

    public static class AddAmounts_Test extends IncomingInvoiceItem_Test {


        @Test
        public void addAmounts_works() throws Exception {

            IncomingInvoiceItem item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());

            // given
            BigDecimal amount = new BigDecimal("100.00");
            item.setNetAmount(amount);
            item.setVatAmount(null);
            item.setGrossAmount(amount);

            // when
            BigDecimal netToAdd = new BigDecimal("50.50");
            BigDecimal vatToAdd = new BigDecimal("10.10");
            BigDecimal grossToAdd = null;
            item.addAmounts(netToAdd, vatToAdd, grossToAdd);

            // then
            assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("150.50"));
            assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("10.10"));
            assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));

        }

    }

    public static class ReasonIncomplete_Test extends IncomingInvoiceItem_Test {


        @Test
        public void reasonIncomplete_works() throws Exception {

            // given
            IncomingInvoiceItem item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());

            // when, then
            assertThat(item.reasonIncomplete()).isEqualTo("incoming invoice type, start date, end date, net amount, vat amount, gross amount, charge required");

            // and when
            item.setStartDate(new LocalDate());
            item.setEndDate(new LocalDate());
            item.setNetAmount(BigDecimal.ZERO);
            item.setVatAmount(BigDecimal.ZERO);
            item.setGrossAmount(BigDecimal.ZERO);
            item.setCharge(new Charge());
            item.setIncomingInvoiceType(IncomingInvoiceType.SERVICE_CHARGES);
            Charge chargeForBudgetItem = new Charge();
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setCharge(chargeForBudgetItem);
            item.setBudgetItem(budgetItem);
            // then
            assertThat(item.reasonIncomplete()).isEqualTo("fixed asset, equal charge on budget item and invoice item required");

            // and when all conditions satisfied
            item.setFixedAsset(new Property());
            item.setCharge(chargeForBudgetItem);
            // then
            assertThat(item.reasonIncomplete()).isNull();

        }

    }

    public static class Validator_Test extends IncomingInvoiceItem_Test {


        @Test
        public void validator_checkNotNull_works() throws Exception {

            String result;

            // given
            IncomingInvoiceItem.Validator validator = new IncomingInvoiceItem.Validator();

            // when condition satisfied
            result = validator.checkNotNull(new Object(), "some property name").getResult();
            // then
            assertThat(result).isNull();

            // and when not conditions satisfied
            result = validator.checkNotNull(null, "some property name").getResult();
            // then
            assertThat(result).isEqualTo("some property name required");

        }

        @Test
        public void validator_validateForIncomingInvoiceType_works() throws Exception {

            String result;
            IncomingInvoiceItem.Validator validator;
            IncomingInvoiceItem item;

            ////// CAPEX //////
            // given
            validator = new IncomingInvoiceItem.Validator();
            item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());
            item.setBudgetItem(new BudgetItem());

            // when
            item.setIncomingInvoiceType(IncomingInvoiceType.CAPEX);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("project (capex), fixed asset, removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setFixedAsset(new Property());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("project (capex), removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setProject(new Project());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            ////// SERVICE_CHARGES //////
            // and given
            validator = new IncomingInvoiceItem.Validator();
            item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());
            item.setProject(new Project());
            // when
            item.setIncomingInvoiceType(IncomingInvoiceType.SERVICE_CHARGES);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("budget item (service charges), fixed asset, removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setFixedAsset(new Property());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("budget item (service charges), removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            BudgetItem budgetItem = new BudgetItem();
            Charge chargeForBudgetItem = new Charge();
            Charge chargeForInvoiceItem = new Charge();
            budgetItem.setCharge(chargeForBudgetItem);
            item.setBudgetItem(budgetItem);
            item.setCharge(chargeForInvoiceItem);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("equal charge on budget item and invoice item, removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setCharge(chargeForBudgetItem);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            ////// PROPERTY_EXPENSES //////
            // and given
            validator = new IncomingInvoiceItem.Validator();
            item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());
            item.setProject(new Project());
            item.setBudgetItem(new BudgetItem());
            // when
            item.setIncomingInvoiceType(IncomingInvoiceType.PROPERTY_EXPENSES);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("fixed asset, removal of budget item (only applicable for service charges), remove project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("fixed asset, removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("fixed asset required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            item.setFixedAsset(new Property());
            // when all conditions satisfied
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            ////// DEFAULT //////
            // and given
            validator = new IncomingInvoiceItem.Validator();
            item = new IncomingInvoiceItem();
            item.setInvoice(new IncomingInvoice());
            item.setProject(new Project());
            item.setBudgetItem(new BudgetItem());
            // when
            item.setIncomingInvoiceType(IncomingInvoiceType.LOCAL_EXPENSES);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges), removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            // and given
            item.setIncomingInvoiceType(IncomingInvoiceType.INTERCOMPANY);
            item.setProject(new Project());
            item.setBudgetItem(new BudgetItem());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // when
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges), removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            // and given
            item.setIncomingInvoiceType(IncomingInvoiceType.TANGIBLE_FIXED_ASSET);
            item.setProject(new Project());
            item.setBudgetItem(new BudgetItem());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges), removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            // and given
            item.setIncomingInvoiceType(IncomingInvoiceType.RE_INVOICING);
            item.setProject(new Project());
            item.setBudgetItem(new BudgetItem());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges), removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();

            // and given
            item.setIncomingInvoiceType(IncomingInvoiceType.CORPORATE_EXPENSES);
            item.setProject(new Project());
            item.setBudgetItem(new BudgetItem());
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges), removal of project (only applicable for capex) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when
            item.setProject(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isEqualTo("removal of budget item (only applicable for service charges) required");

            // and given
            validator = new IncomingInvoiceItem.Validator();
            // when all conditions satisfied
            item.setBudgetItem(null);
            result = validator.validateForIncomingInvoiceType(item).getResult();
            // then
            assertThat(result).isNull();
        }

    }

    public static class CopyChargeAndProjectFromSingleLinkedOrderItemIfAny_Test extends IncomingInvoiceItem_Test {


        @Test
        public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny_no_item_works() throws Exception {

            // given
            IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
            invoiceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

            // expect
            context.checking(new Expectations(){{
                oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
            }});

            // when
            invoiceItem.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();

        }

        @Test
        public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny_two_items_works() throws Exception {

            // given
            IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
            invoiceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
            OrderItemInvoiceItemLink link1 = new OrderItemInvoiceItemLink();
            OrderItemInvoiceItemLink link2 = new OrderItemInvoiceItemLink();

            // expect
            context.checking(new Expectations(){{
                oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
                will(returnValue(Arrays.asList(link1, link2)));
            }});

            // when
            invoiceItem.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();

            // then
            assertThat(invoiceItem.getCharge()).isNull();
            assertThat(invoiceItem.getProject()).isNull();

        }


        @Test
        public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny_one_item_works() throws Exception {

            // given
            IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem(){
                @Override
                void invalidateApproval(){}
            };
            invoiceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

            OrderItem orderItem = new OrderItem();
            Charge charge = new Charge();
            orderItem.setCharge(charge);
            Project project = new Project();
            orderItem.setProject(project);
            Property property = new Property();
            orderItem.setProperty(property);

            OrderItemInvoiceItemLink link = new OrderItemInvoiceItemLink();
            link.setOrderItem(orderItem);

            // expect
            context.checking(new Expectations(){{
                allowing(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
                will(returnValue(Arrays.asList(link)));
            }});

            // when
            invoiceItem.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();

            // then
            assertThat(invoiceItem.getCharge()).isEqualTo(charge);
            assertThat(invoiceItem.getProject()).isEqualTo(project);
            assertThat(invoiceItem.getFixedAsset()).isEqualTo(property);

        }

    }

    public static class ChargeIsImmutableReason_Test extends IncomingInvoiceItem_Test {

        private void expectOrderItemInvoiceItemLinkRepository_returns(final List<OrderItemInvoiceItemLink> result) {
            context.checking(new Expectations(){{
                allowing(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(item);
                will(returnValue(result));
            }});
        }

        IncomingInvoiceItem item;

        @Before
        public void setUp() throws Exception {
            item = new IncomingInvoiceItem(){
                @Override
                void invalidateApproval(){}
            };

            item.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        }

        @Test
        public void no_links() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.chargeIsImmutableReason();

            // then
            assertThat(s).isNull();
        }

        @Test
        public void is_linked_to_budget_item() throws Exception {

            // given
            item.setBudgetItem(new BudgetItem());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.chargeIsImmutableReason();

            assertThat(s).isEqualTo("Charge cannot be changed because invoice item is linked to a budget");
        }

        @Test
        public void is_linked_to_order_item() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.singletonList(new OrderItemInvoiceItemLink()));

            // when
            final String s = item.chargeIsImmutableReason();

            assertThat(s).isEqualTo("Charge cannot be changed because invoice item is linked to an order");
        }

        @Test
        public void is_a_reversal() throws Exception {

            // given
            item.setReversalOf(new IncomingInvoiceItem());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.chargeIsImmutableReason();

            assertThat(s).isEqualTo("Charge cannot be changed because invoice item is a reversal of another item");
        }

        @Test
        public void has_been_reported() throws Exception {

            // given
            item.setReportedDate(LocalDate.now());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.chargeIsImmutableReason();

            assertThat(s).isEqualTo("Charge cannot be changed because invoice item has already been reported");
        }


    }

    public static class BudgetIsImmutableReason_Test extends IncomingInvoiceItem_Test {

        private void expectOrderItemInvoiceItemLinkRepository_returns(final List<OrderItemInvoiceItemLink> result) {
            context.checking(new Expectations(){{
                allowing(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(item);
                will(returnValue(result));
            }});
        }

        IncomingInvoiceItem item;

        @Before
        public void setUp() throws Exception {
            item = new IncomingInvoiceItem(){
                @Override
                void invalidateApproval(){}
            };
            item.setInvoice(new IncomingInvoice() {
                @Override public IncomingInvoiceType getType() {
                    return IncomingInvoiceType.SERVICE_CHARGES;
                }
            });

            item.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        }

        @Test
        public void no_links() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.budgetItemIsImmutableReason();

            // then
            assertThat(s).isNull();
        }

        @Test
        public void is_not_for_service_charge() throws Exception {

            // given
            item.setInvoice(new IncomingInvoice() {
                @Override public IncomingInvoiceType getType() {
                    return IncomingInvoiceType.CAPEX;
                }
            });

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.budgetItemIsImmutableReason();

            assertThat(s).isEqualTo("Budget item cannot be changed because invoice item parent invoice is not for service charges");
        }

        @Test
        public void is_linked_to_order_item() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.singletonList(new OrderItemInvoiceItemLink()));

            // when
            final String s = item.budgetItemIsImmutableReason();

            assertThat(s).isEqualTo("Budget item cannot be changed because invoice item is linked to an order");
        }

        @Test
        public void is_a_reversal() throws Exception {

            // given
            item.setReversalOf(new IncomingInvoiceItem());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.budgetItemIsImmutableReason();

            assertThat(s).isEqualTo("Budget item cannot be changed because invoice item is a reversal of another item");
        }

        @Test
        public void has_been_reported() throws Exception {

            // given
            item.setReportedDate(LocalDate.now());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.budgetItemIsImmutableReason();

            assertThat(s).isEqualTo("Budget item cannot be changed because invoice item has already been reported");
        }


    }

    public static class ProjectIsImmutableReason_Test extends IncomingInvoiceItem_Test {

        private void expectOrderItemInvoiceItemLinkRepository_returns(final List<OrderItemInvoiceItemLink> result) {
            context.checking(new Expectations(){{
                allowing(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(item);
                will(returnValue(result));
            }});
        }

        IncomingInvoiceItem item;

        @Before
        public void setUp() throws Exception {
            item = new IncomingInvoiceItem(){
                @Override
                void invalidateApproval(){}
            };

            item.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        }

        @Test
        public void no_links() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.projectIsImmutableReason();

            // then
            assertThat(s).isNull();
        }

        @Test
        public void is_linked_to_order_item() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.singletonList(new OrderItemInvoiceItemLink()));

            // when
            final String s = item.projectIsImmutableReason();

            assertThat(s).isEqualTo("Project cannot be changed because invoice item is linked to an order");
        }

        @Test
        public void is_a_reversal() throws Exception {

            // given
            item.setReversalOf(new IncomingInvoiceItem());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.projectIsImmutableReason();

            assertThat(s).isEqualTo("Project cannot be changed because invoice item is a reversal of another item");
        }

        @Test
        public void has_been_reported() throws Exception {

            // given
            item.setReportedDate(LocalDate.now());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.projectIsImmutableReason();

            assertThat(s).isEqualTo("Project cannot be changed because invoice item has already been reported");
        }


    }

    public static class FixedAssetIsImmutableReason_Test extends IncomingInvoiceItem_Test {

        private void expectOrderItemInvoiceItemLinkRepository_returns(final List<OrderItemInvoiceItemLink> result) {
            context.checking(new Expectations(){{
                allowing(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(item);
                will(returnValue(result));
            }});
        }

        IncomingInvoiceItem item;

        @Before
        public void setUp() throws Exception {
            item = new IncomingInvoiceItem(){
                @Override
                void invalidateApproval(){}
            };

            item.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        }

        @Test
        public void no_links() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.fixedAssetIsImmutableReason();

            // then
            assertThat(s).isNull();
        }

        @Test
        public void is_linked_to_budget_item() throws Exception {

            // given
            item.setBudgetItem(new BudgetItem());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.fixedAssetIsImmutableReason();

            assertThat(s).isEqualTo("Fixed asset cannot be changed because invoice item is linked to a budget");
        }

        @Test
        public void is_linked_to_project() throws Exception {

            // given
            item.setProject(new Project());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.fixedAssetIsImmutableReason();

            assertThat(s).isEqualTo("Fixed asset cannot be changed because invoice item is linked to a project");
        }


        @Test
        public void is_linked_to_order_item() throws Exception {

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.singletonList(new OrderItemInvoiceItemLink()));

            // when
            final String s = item.fixedAssetIsImmutableReason();

            assertThat(s).isEqualTo("Fixed asset cannot be changed because invoice item is linked to an order");
        }

        @Test
        public void is_a_reversal() throws Exception {

            // given
            item.setReversalOf(new IncomingInvoiceItem());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.fixedAssetIsImmutableReason();

            assertThat(s).isEqualTo("Fixed asset cannot be changed because invoice item is a reversal of another item");
        }

        @Test
        public void has_been_reported() throws Exception {

            // given
            item.setReportedDate(LocalDate.now());

            // expect
            expectOrderItemInvoiceItemLinkRepository_returns(Collections.emptyList());

            // when
            final String s = item.fixedAssetIsImmutableReason();

            assertThat(s).isEqualTo("Fixed asset cannot be changed because invoice item has already been reported");
        }


    }

}