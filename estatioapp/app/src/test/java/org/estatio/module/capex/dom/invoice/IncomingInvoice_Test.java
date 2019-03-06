package org.estatio.module.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.metamodel.MetaModelService2;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.tax.dom.Tax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState.COMPLETED;
import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState.NEW;
import static org.incode.module.unittestsupport.dom.matchers.IsisMatchers.anInstanceOf;

public class IncomingInvoice_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceItemRepository mockIncomingInvoiceItemRepository;

    @Mock
    EventBusService mockEventBusService;

    States eventBusInteractions = context.states("not-recognised");

    IncomingInvoice invoice;

    @Before
    public void setUp() throws Exception {
        invoice = new IncomingInvoice() {
            @Override
            protected EventBusService getEventBusService() {
                return mockEventBusService;
            }

            @Override
            public String getAtPath() {
                return "/FRA";
            }
        };
        context.checking(new Expectations() {{
            ignoring(mockEventBusService);
            when(eventBusInteractions.isNot("recognised"));
        }});
    }

    public static class reasonIncomplete_Test extends IncomingInvoice_Test {

        @Test
        public void scenario() throws Exception {

            // given
            invoice.setPaymentMethod(PaymentMethod.MANUAL_PROCESS);
            BankAccount bankAccount = new BankAccount();
            invoice.setBankAccount(bankAccount);

            IncomingInvoiceItem item1 = new IncomingInvoiceItem();
            item1.setSequence(BigInteger.ONE);
            invoice.getItems().add(item1);
            item1.setInvoice(invoice);

            // when neither conditions on invoice and items satisfied
            String result = invoice.reasonIncomplete();
            // then
            assertThat(result).isEqualTo("incoming invoice type, invoice number, buyer, seller, date received, due date, net amount, gross amount, (on item 1) incoming invoice type, start date, end date, net amount, vat amount, gross amount, charge required");

            // and when conditions on item satisfied
            invoice.setType(IncomingInvoiceType.PROPERTY_EXPENSES);
            invoice.setInvoiceNumber("123");
            invoice.setNetAmount(new BigDecimal("100"));
            item1.setIncomingInvoiceType(IncomingInvoiceType.LOCAL_EXPENSES);
            item1.setStartDate(new LocalDate());
            item1.setEndDate(new LocalDate());
            item1.setNetAmount(new BigDecimal("100"));
            item1.setGrossAmount(new BigDecimal("100"));
            item1.setVatAmount(BigDecimal.ZERO);
            item1.setCharge(new Charge());
            result = invoice.reasonIncomplete();
            // then
            assertThat(result).isEqualTo("buyer, seller, date received, due date, gross amount, property required");

            // and when conditions for invoice satisfied
            item1.setIncomingInvoiceType(IncomingInvoiceType.CAPEX);
            invoice.setBuyer(new Organisation());
            Organisation seller = new Organisation();
            invoice.setSeller(seller);
            bankAccount.setOwner(seller);
            invoice.setBankAccount(bankAccount);
            invoice.setDateReceived(new LocalDate());
            invoice.setDueDate(new LocalDate());
            invoice.setGrossAmount(BigDecimal.ZERO);
            invoice.setProperty(new Property());
            result = invoice.reasonIncomplete();
            // then
            assertThat(result).isEqualTo("total amount on items equal to amount on the invoice, (on item 1) project (capex), fixed asset required");

            // and when all conditions satisfied
            item1.setFixedAsset(new Property());
            item1.setProject(new Project());
            invoice.setGrossAmount(new BigDecimal("100"));
            invoice.setNetAmount(new BigDecimal("100"));
            result = invoice.reasonIncomplete();
            // then
            assertThat(result).isNull();

        }
    }

    public static class validator_Test extends IncomingInvoice_Test {

        @Test
        public void checkNotNull() throws Exception {

            String result;

            // given
            IncomingInvoice.Validator validator = new IncomingInvoice.Validator();

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
        public void validateForIncomingInvoiceType() throws Exception {

            String result;
            IncomingInvoice.Validator validator;

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setType(IncomingInvoiceType.CAPEX);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isEqualTo("property required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setType(IncomingInvoiceType.PROPERTY_EXPENSES);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isEqualTo("property required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setType(IncomingInvoiceType.SERVICE_CHARGES);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isEqualTo("property required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // and when all conditions satisfied
            invoice.setType(IncomingInvoiceType.LOCAL_EXPENSES);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isNull();

            // and when all conditions satisfied
            invoice.setType(IncomingInvoiceType.INTERCOMPANY);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isNull();

            // and when all conditions satisfied
            invoice.setType(IncomingInvoiceType.TANGIBLE_FIXED_ASSET);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isNull();

            // and when all conditions satisfied
            invoice.setType(IncomingInvoiceType.RE_INVOICING);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isNull();

            // and when all conditions satisfied
            invoice.setType(IncomingInvoiceType.CORPORATE_EXPENSES);
            result = validator.validateForIncomingInvoiceType(invoice).getResult();
            // then
            assertThat(result).isNull();

        }

        @Test
        public void validateForPaymentMethod() throws Exception {

            String result;
            IncomingInvoice.Validator validator;

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
            result = validator.validateForPaymentMethod(invoice).getResult();
            // then
            assertThat(result).isEqualTo("bank account required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setPaymentMethod(PaymentMethod.BILLING_ACCOUNT);
            result = validator.validateForPaymentMethod(invoice).getResult();
            // then
            assertThat(result).isEqualTo("bank account required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setPaymentMethod(PaymentMethod.CASH);
            result = validator.validateForPaymentMethod(invoice).getResult();
            // then
            assertThat(result).isEqualTo("bank account required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setPaymentMethod(PaymentMethod.CHEQUE);
            result = validator.validateForPaymentMethod(invoice).getResult();
            // then
            assertThat(result).isEqualTo("bank account required");

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            result = validator.validateForPaymentMethod(invoice).getResult();
            // then
            assertThat(result).isNull();

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            // when
            invoice.setPaymentMethod(PaymentMethod.MANUAL_PROCESS);
            result = validator.validateForPaymentMethod(invoice).getResult();
            // then
            assertThat(result).isNull();

        }

        @Test
        public void validateForAmounts() throws Exception {

            String result;
            IncomingInvoice.Validator validator;

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            invoice.setNetAmount(new BigDecimal("100.00"));
            invoice.setGrossAmount(new BigDecimal("100.00"));

            // when
            result = validator.validateForAmounts(invoice).getResult();

            // then
            Assertions.assertThat(result).isEqualTo("total amount on items equal to amount on the invoice required");

            // and given
            invoice.setGrossAmount(BigDecimal.ZERO);
            invoice.setNetAmount(BigDecimal.ZERO);
            // when
            validator = new IncomingInvoice.Validator();
            result = validator.validateForAmounts(invoice).getResult();
            // then
            Assertions.assertThat(result).isNull();

            // and given
            invoice.setNetAmount(new BigDecimal("100.00"));
            invoice.setGrossAmount(new BigDecimal("100.00"));
            IncomingInvoiceItem item = new IncomingInvoiceItem() {
                @Override
                void invalidateApproval() {
                    // nothing
                }
            };
            item.setNetAmount(new BigDecimal("100.00"));
            item.setGrossAmount(new BigDecimal("100.00"));
            invoice.getItems().add(item);
            // when
            validator = new IncomingInvoice.Validator();
            result = validator.validateForAmounts(invoice).getResult();
            // then
            Assertions.assertThat(result).isNull();

        }

        @Test
        public void validateForBankAccountOwner() throws Exception {

            String result;
            IncomingInvoice.Validator validator;

            // given
            validator = new IncomingInvoice.Validator();
            invoice = new IncomingInvoice();
            Organisation seller = new Organisation();
            invoice.setSeller(seller);
            Organisation bankAccountOwner = new Organisation();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setOwner(bankAccountOwner);
            invoice.setBankAccount(bankAccount);

            // when
            result = validator.validateForBankAccountOwner(invoice).getResult();

            // then
            Assertions.assertThat(invoice.getSeller()).isNotEqualTo(invoice.getBankAccount().getOwner());
            Assertions.assertThat(result).isEqualTo("match of owner bankaccount and seller required");

            // and given
            bankAccount.setOwner(seller);

            // when
            validator = new IncomingInvoice.Validator();
            result = validator.validateForBankAccountOwner(invoice).getResult();
            // then
            Assertions.assertThat(result).isNull();

        }
    }

    public static class splitItem_Test extends IncomingInvoice_Test {

        @Test
        public void happy_case() throws Exception {

            // given
            invoice.setType(IncomingInvoiceType.CAPEX);
            invoice.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;

            LocalDate dueDate = new LocalDate(2018, 01, 01);
            invoice.setDueDate(dueDate);

            String description = "some description";
            Tax tax = new Tax();
            Charge charge = new Charge();
            Property property = new Property();
            Project project = new Project();
            BudgetItem budgetItem = new BudgetItem();
            String period = "F2018";

            IncomingInvoiceItem itemToSplit = new IncomingInvoiceItem();
            itemToSplit.setInvoice(invoice);

            itemToSplit.setNetAmount(new BigDecimal("200.00"));
            itemToSplit.setVatAmount(new BigDecimal("40.00"));
            itemToSplit.setGrossAmount(new BigDecimal("240.00"));

            BigDecimal newItemNetAmount = new BigDecimal("50.00");
            BigDecimal newItemVatAmount = new BigDecimal("10");
            BigDecimal newItemGrossAmount = new BigDecimal("60.00");

            // expect
            context.checking(new Expectations() {{
                oneOf(mockIncomingInvoiceItemRepository).addItem(
                        invoice,
                        IncomingInvoiceType.CAPEX,
                        charge,
                        description,
                        newItemNetAmount,
                        newItemVatAmount,
                        newItemGrossAmount,
                        tax,
                        dueDate,
                        period,
                        property,
                        project,
                        budgetItem);
            }});

            // when
            invoice.splitItem(itemToSplit, description, newItemNetAmount, newItemVatAmount, tax, newItemGrossAmount, charge, property, project, budgetItem, period);

            // then
            assertThat(itemToSplit.getNetAmount()).isEqualTo(new BigDecimal("150.00"));
            assertThat(itemToSplit.getVatAmount()).isEqualTo(new BigDecimal("30.00"));
            assertThat(itemToSplit.getGrossAmount()).isEqualTo(new BigDecimal("180.00"));

        }
    }

    public static class mergeItem_Test extends IncomingInvoice_Test {

        @Test
        public void happy_case() throws Exception {

            // given
            invoice.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;

            IncomingInvoiceItem sourceItem = new IncomingInvoiceItem();
            IncomingInvoiceItem targetItem = new IncomingInvoiceItem();

            // expect
            context.checking(new Expectations() {{
                oneOf(mockIncomingInvoiceItemRepository).mergeItems(
                        sourceItem, targetItem);
            }});

            // when
            invoice.mergeItems(sourceItem, targetItem);

        }
    }

    public static class reasonDisabledDueToState_Test extends IncomingInvoice_Test {

        @Ignore // WIP
        @Test
        public void wip() {

            invoice.reasonDisabledDueToState(invoice);

        }
    }

    public static class reasonDisabledDueToApprovalStateIfAny_Test extends IncomingInvoice_Test {

        IncomingInvoice incomingInvoice;
        IncomingInvoiceApprovalState state;

        @Mock
        MetaModelService3 mockMetaModelService3;

        @Before
        public void setUp() throws Exception {
            incomingInvoice = new IncomingInvoice() {
                @Override
                public IncomingInvoiceApprovalState getApprovalState() {
                    return state;
                }
            };
            incomingInvoice.metaModelService3 = mockMetaModelService3;
        }

        @Test
        public void when_state_unknown() throws Exception {

            // given
            state = null;

            // when
            final IncomingInvoice viewContext = incomingInvoice;
            final String reason = incomingInvoice.reasonDisabledDueToApprovalStateIfAny(viewContext);

            // then
            assertThat(reason).isEqualTo("Cannot modify invoice because invoice state is unknown (was migrated so assumed to be approved)");

        }

        @Test
        public void when_state_new() throws Exception {

            // given
            state = NEW;

            // when
            final IncomingInvoice viewContext = incomingInvoice;
            final String reason = incomingInvoice.reasonDisabledDueToApprovalStateIfAny(viewContext);

            // then
            assertThat(reason).isNull();

        }

        @Test
        public void when_state_completed_and_entity() throws Exception {

            // given
            state = IncomingInvoiceApprovalState.COMPLETED;

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockMetaModelService3).sortOf(incomingInvoice.getClass());
                will(returnValue(MetaModelService2.Sort.JDO_ENTITY));
            }});

            // when
            final IncomingInvoice viewContext = incomingInvoice;
            final String reason = incomingInvoice.reasonDisabledDueToApprovalStateIfAny(viewContext);

            // then
            assertThat(reason).isNull();

        }

        @Test
        public void when_state_completed_and_view_model() throws Exception {

            // given
            state = IncomingInvoiceApprovalState.COMPLETED;

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockMetaModelService3).sortOf(incomingInvoice.getClass());
                will(returnValue(MetaModelService2.Sort.VIEW_MODEL));
            }});

            // when
            final IncomingInvoice viewContext = incomingInvoice;
            final String reason = incomingInvoice.reasonDisabledDueToApprovalStateIfAny(viewContext);

            // then
            assertThat(reason).isEqualTo("Cannot modify invoice because modification through view not allowed once invoice is COMPLETED");

        }

        @Test
        public void when_state_other() throws Exception {

            for (final IncomingInvoiceApprovalState state :
                    Arrays.asList(IncomingInvoiceApprovalState.values()).stream().
                            filter(x -> x != NEW && x != COMPLETED)
                            .collect(Collectors.toList())) {

                // given
                this.state = state;

                // when
                final IncomingInvoice viewContext = incomingInvoice;
                final String reason = incomingInvoice.reasonDisabledDueToApprovalStateIfAny(viewContext);

                // then
                assertThat(reason).isEqualTo("Cannot modify invoice because invoice is in state of " + state);
            }

        }

    }

    public static class invalidateApprovalIfDiffer_Test extends IncomingInvoice_Test {

        @Test
        public void when_dont_differ_both_null() {

            // given
            invoice.setGrossAmount(null);
            eventBusInteractions.become("recognised");

            // expect
            context.checking(new Expectations() {{
                never(mockEventBusService);
            }});

            // when
            invoice.setGrossAmount(null);

        }

        @Test
        public void when_dont_differ_both_non_null() {

            // given
            invoice.setGrossAmount(new BigDecimal("123.45"));
            eventBusInteractions.become("recognised");

            // expect
            context.checking(new Expectations() {{
                never(mockEventBusService);
            }});

            // when
            invoice.setGrossAmount(new BigDecimal("123.45"));

        }

        @Test
        public void when_differ_was_null() {

            // given
            invoice.setGrossAmount(null);
            eventBusInteractions.become("recognised");

            // expect
            context.checking(new Expectations() {{
                oneOf(mockEventBusService).post(with(anInstanceOf(IncomingInvoice.ApprovalInvalidatedEvent.class)));
            }});

            // when
            invoice.setGrossAmount(new BigDecimal("123.45"));

        }

        @Test
        public void when_differ_becomes_null() {

            // given
            invoice.setGrossAmount(new BigDecimal("123.45"));
            eventBusInteractions.become("recognised");

            // expect
            context.checking(new Expectations() {{
                oneOf(mockEventBusService).post(with(anInstanceOf(IncomingInvoice.ApprovalInvalidatedEvent.class)));
            }});

            // when
            invoice.setGrossAmount(null);

        }

        @Test
        public void when_differ() {

            // given
            invoice.setGrossAmount(new BigDecimal("123.45"));
            eventBusInteractions.become("recognised");

            // expect
            context.checking(new Expectations() {{
                oneOf(mockEventBusService).post(with(anInstanceOf(IncomingInvoice.ApprovalInvalidatedEvent.class)));
            }});

            // when
            invoice.setGrossAmount(new BigDecimal("543.21"));

        }

    }

    public static class reasonDisabledDueToStateStrict_Test extends IncomingInvoice_Test {
        @Test
        public void when_migrated() {
            // given
            invoice.setApprovalState(null);

            // when
            final String reason = invoice.reasonDisabledDueToStateStrict();

            // then
            assertThat(reason).contains("Cannot modify", "migrated");
        }

        @Test
        public void when_new() {
            // given
            invoice.setApprovalState(NEW);

            // when
            final String reason = invoice.reasonDisabledDueToStateStrict();

            // then
            assertThat(reason).isNull();
        }

        @Test
        public void when_anything_else() {

            Arrays.stream(IncomingInvoiceApprovalState.values())
                    .filter(state -> state != NEW)
                    .forEach(state -> {

                        // given
                        invoice.setApprovalState(state);

                        // when
                        final String reason = invoice.reasonDisabledDueToStateStrict();

                        // then
                        assertThat(reason).contains("Cannot modify").doesNotContain("migrated");
                    });
        }

    }

    public static class ChangeAmounts extends IncomingInvoice_Test {

        @Test
        public void validateChangeAmounts_works() throws Exception {

            // given, when, then
            assertThat(invoice.validateChangeAmounts(new BigDecimal("10.00"), new BigDecimal("9.99"))).isEqualTo("Gross amount cannot be lower than net amount");
            assertThat(invoice.validateChangeAmounts(new BigDecimal("10.00"), new BigDecimal("10.00"))).isNull();

            // given, when, then
            assertThat(invoice.validateChangeAmounts(new BigDecimal("-10.00"), new BigDecimal("-9.99"))).isEqualTo("Gross amount cannot be lower than net amount");
            assertThat(invoice.validateChangeAmounts(new BigDecimal("-10.00"), new BigDecimal("-10.00"))).isNull();
        }

    }

    public static class AmountAreCovered extends IncomingInvoice_Test {

        @Test
        public void amountsAreCovered_test() throws Exception {

            // given
            assertThat(invoice.getNetAmount()).isNull();
            assertThat(invoice.getGrossAmount()).isNull();
            assertThat(invoice.amountsCoveredByAmountsItems()).isFalse();

            // when
            invoice.setNetAmount(new BigDecimal("10.00"));
            // then
            assertThat(invoice.amountsCoveredByAmountsItems()).isFalse();

            // and given
            assertThat(invoice.getNetAmount()).isEqualTo("10.00");
            assertThat(invoice.getGrossAmount()).isNull();
            // and when
            IncomingInvoiceItem item = new IncomingInvoiceItem() {
                @Override
                void invalidateApproval() {
                    // nothing
                }
            };
            item.setNetAmount(new BigDecimal("9.99"));
            invoice.getItems().add(item);

            // then
            assertThat(invoice.amountsCoveredByAmountsItems()).isFalse();

            // and given
            assertThat(invoice.getNetAmount()).isEqualTo("10.00");
            assertThat(invoice.getGrossAmount()).isNull();
            // and when
            item.setNetAmount(new BigDecimal("10.00"));
            // then
            assertThat(invoice.amountsCoveredByAmountsItems()).isTrue();

            // and when
            invoice.setGrossAmount(new BigDecimal("12.00"));
            // then still
            assertThat(invoice.amountsCoveredByAmountsItems()).isTrue();

            // and given
            assertThat(invoice.getNetAmount()).isEqualTo("10.00");
            assertThat(invoice.getGrossAmount()).isEqualTo("12.00");
            assertThat(item.getGrossAmount()).isNull();
            // and when
            item.setNetAmount(new BigDecimal("9.99"));
            // then
            assertThat(invoice.amountsCoveredByAmountsItems()).isFalse();

            // and given
            assertThat(invoice.getNetAmount()).isEqualTo("10.00");
            assertThat(invoice.getGrossAmount()).isEqualTo("12.00");
            assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("9.99"));
            // and when
            item.setGrossAmount(new BigDecimal("11.99"));
            // then still
            assertThat(invoice.amountsCoveredByAmountsItems()).isFalse();

            // and given
            assertThat(invoice.getNetAmount()).isEqualTo("10.00");
            assertThat(invoice.getGrossAmount()).isEqualTo("12.00");
            assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("9.99"));
            // and when
            item.setGrossAmount(new BigDecimal("12.00"));
            // then
            assertThat(invoice.amountsCoveredByAmountsItems()).isTrue();

            // and given
            assertThat(invoice.getNetAmount()).isEqualTo("10.00");
            assertThat(invoice.getGrossAmount()).isEqualTo("12.00");
            assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("12.00"));
            // and when
            item.setNetAmount(new BigDecimal("10.00"));
            // then still
            assertThat(invoice.amountsCoveredByAmountsItems()).isTrue();

        }

    }

    public static class IsReported extends IncomingInvoice_Test {

        @Test
        public void is_reported_works() throws Exception {

            // given
            IncomingInvoiceItem unreportedItem = new IncomingInvoiceItem();
            invoice.getItems().add(unreportedItem);

            // when then
            assertThat(invoice.isReported()).isFalse();

            // and when
            IncomingInvoiceItem reportedItem = new IncomingInvoiceItem();
            reportedItem.setUuid(UUID.randomUUID().toString()); // set for compare in sorted set invoice#getItems()
            reportedItem.setReportedDate(new LocalDate(2017, 1, 1));
            invoice.getItems().add(reportedItem);

            // then
            assertThat(invoice.isReported()).isTrue();

        }

    }

    public static class ItemsIgnoringReversals extends IncomingInvoice_Test {

        @Test
        public void reportedItemsIgnoringReversals_works() {

            //given
            IncomingInvoiceItem reportedUnreversedItem = new IncomingInvoiceItem();
            reportedUnreversedItem.setReportedDate(new LocalDate());
            reportedUnreversedItem.setSequence(BigInteger.valueOf(1));

            IncomingInvoiceItem reversal1 = new IncomingInvoiceItem();
            reversal1.setReversalOf(new IncomingInvoiceItem());
            reversal1.setReportedDate(new LocalDate());
            reversal1.setSequence(BigInteger.valueOf(2));

            IncomingInvoiceItem unreportedItem = new IncomingInvoiceItem();
            unreportedItem.setSequence(BigInteger.valueOf(3));

            IncomingInvoiceItem reportedReversedItem = new IncomingInvoiceItem();
            reportedUnreversedItem.setReportedDate(new LocalDate());
            reportedUnreversedItem.setSequence(BigInteger.valueOf(4));

            IncomingInvoiceItem reversal2 = new IncomingInvoiceItem();
            reversal2.setReversalOf(reportedReversedItem);
            reversal2.setReportedDate(new LocalDate());
            reversal2.setSequence(BigInteger.valueOf(5));

            invoice.getItems().addAll(Arrays.asList(reportedUnreversedItem, reversal1, unreportedItem, reportedReversedItem, reversal2));
            assertThat(invoice.getItems().size()).isEqualTo(5);

            // when
            List<IncomingInvoiceItem> result = invoice.reportedItemsIgnoringReversals();

            // then
            Assertions.assertThat(result.size()).isEqualTo(1);
            Assertions.assertThat(result.get(0)).isEqualTo(reportedUnreversedItem);

        }

        @Test
        public void unreportedItemsIgnoringReversals_works() {

            //given
            IncomingInvoiceItem reportedItem = new IncomingInvoiceItem();
            reportedItem.setReportedDate(new LocalDate());
            reportedItem.setSequence(BigInteger.valueOf(1));
            IncomingInvoiceItem reversal = new IncomingInvoiceItem();
            reversal.setReversalOf(new IncomingInvoiceItem());
            reversal.setSequence(BigInteger.valueOf(2));
            IncomingInvoiceItem unreportedItem = new IncomingInvoiceItem();
            unreportedItem.setSequence(BigInteger.valueOf(3));
            invoice.getItems().addAll(Arrays.asList(reportedItem, reversal, unreportedItem));

            // when
            List<IncomingInvoiceItem> result = invoice.unreportedItemsIgnoringReversals();

            // then
            Assertions.assertThat(result.size()).isEqualTo(1);
            Assertions.assertThat(result.get(0)).isEqualTo(unreportedItem);

        }

    }

    public static class SetTypeOnUnreportedItems extends IncomingInvoice_Test {

        @Test
        public void setTypeOnUnreportedItems_Ignoring_Reversals_works() throws Exception {

            // given
            IncomingInvoiceItem reportedItem = new IncomingInvoiceItem();
            reportedItem.setSequence(BigInteger.valueOf(1));
            reportedItem.setReportedDate(new LocalDate());
            IncomingInvoiceItem reversal = new IncomingInvoiceItem();
            reversal.setSequence(BigInteger.valueOf(2));
            reversal.setReversalOf(new IncomingInvoiceItem());
            IncomingInvoiceItem itemToBeModified = new IncomingInvoiceItem() {
                @Override
                void invalidateApproval() {
                }
            };
            itemToBeModified.setSequence(BigInteger.valueOf(3));
            invoice.getItems().addAll(Arrays.asList(reportedItem, reversal, itemToBeModified));

            // when
            final IncomingInvoiceType typeCapex = IncomingInvoiceType.CAPEX;
            invoice.setTypeOnUnreportedItems(typeCapex);

            // then
            Assertions.assertThat(itemToBeModified.getIncomingInvoiceType()).isEqualTo(typeCapex);

        }

    }

    public static class ReversedItems extends IncomingInvoice_Test {

        @Test
        public void reversals_and_reversedItems_methods_work() throws Exception {

            // given
            IncomingInvoiceItem reversedItem = new IncomingInvoiceItem();
            reversedItem.setSequence(BigInteger.valueOf(1));
            IncomingInvoiceItem reversal = new IncomingInvoiceItem();
            reversal.setReversalOf(reversedItem);
            reversal.setSequence(BigInteger.valueOf(2));

            invoice.getItems().addAll(Arrays.asList(reversedItem, reversal));

            // when // then
            assertThat(invoice.reversals().size()).isEqualTo(1);
            assertThat(invoice.reversals()).contains(reversal);
            assertThat(invoice.reversedItems().size()).isEqualTo(1);
            assertThat(invoice.reversedItems()).contains(reversedItem);

        }

    }

    @Mock
    IncomingInvoiceApprovalStateTransition.Repository mockStateTransitionRepository;

    public static class Approvals extends IncomingInvoice_Test {

        @Test
        public void get_approvals_works() throws Exception {

            // given
            IncomingInvoice invoice = new IncomingInvoice();
            invoice.stateTransitionRepository = mockStateTransitionRepository;

            IncomingInvoiceApprovalStateTransition tr1 = new IncomingInvoiceApprovalStateTransition();
            tr1.setToState(IncomingInvoiceApprovalState.APPROVED);

            IncomingInvoiceApprovalStateTransition tr2 = new IncomingInvoiceApprovalStateTransition();
            tr2.setToState(IncomingInvoiceApprovalState.COMPLETED);

            IncomingInvoiceApprovalStateTransition tr3 = new IncomingInvoiceApprovalStateTransition();
            tr3.setToState(IncomingInvoiceApprovalState.APPROVED);
            tr3.setCompletedBy("Manager1");
            tr3.setCompletedOn(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDateTime("2017-01-01"));

            IncomingInvoiceApprovalStateTransition tr4 = new IncomingInvoiceApprovalStateTransition();
            tr4.setToState(IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR);
            tr4.setCompletedBy("Manager2");
            tr4.setCompletedOn(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDateTime("2017-01-03"));

            IncomingInvoiceApprovalStateTransition tr5 = new IncomingInvoiceApprovalStateTransition();
            tr5.setToState(IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER);
            tr5.setCompletedBy("Manager3");
            tr5.setCompletedOn(DateTimeFormat.forPattern("yyyy-MM-dd")
                    .parseLocalDateTime("2017-01-02"));

            IncomingInvoiceApprovalStateTransition tr6 = new IncomingInvoiceApprovalStateTransition();

            // expect
            context.checking(new Expectations() {{
                oneOf(mockStateTransitionRepository).findByDomainObject(invoice);
                will(returnValue(Arrays.asList(tr1, tr2, tr3, tr4, tr5, tr6)));
            }});

            // when
            List<IncomingInvoice.ApprovalString> approvals = invoice.getApprovals();

            // then
            assertThat(approvals.size()).isEqualTo(3);
            assertThat(approvals.get(0).getCompletedBy()).isEqualTo(tr3.getCompletedBy());
            assertThat(approvals.get(0).getCompletedOn()).isEqualTo("01-Jan-2017 00:00");
            assertThat(approvals.get(1).getCompletedBy()).isEqualTo(tr5.getCompletedBy());
            assertThat(approvals.get(1).getCompletedOn()).isEqualTo("02-Jan-2017 00:00");
            assertThat(approvals.get(2).getCompletedBy()).isEqualTo(tr4.getCompletedBy());
            assertThat(approvals.get(2).getCompletedOn()).isEqualTo("03-Jan-2017 00:00");

        }

    }

    public static class Summaries_Test extends IncomingInvoice_Test {

        IncomingInvoiceItem reversedItem;
        IncomingInvoiceItem reversal;
        IncomingInvoiceItem regularItem1;
        IncomingInvoiceItem regularItem2;

        @Before
        public void setup() {

            reversedItem = new IncomingInvoiceItem();
            reversedItem.setSequence(BigInteger.valueOf(1));
            reversedItem.setInvoice(invoice);

            reversal = new IncomingInvoiceItem();
            reversal.setReversalOf(reversedItem);
            reversal.setSequence(BigInteger.valueOf(2));
            reversal.setInvoice(invoice);

            regularItem1 = new IncomingInvoiceItem();
            regularItem1.setSequence(BigInteger.valueOf(3));
            regularItem1.setInvoice(invoice);

            regularItem2 = new IncomingInvoiceItem();
            regularItem2.setSequence(BigInteger.valueOf(4));
            regularItem2.setInvoice(invoice);

            invoice.getItems().addAll(Arrays.asList(regularItem2, reversedItem, reversal, regularItem1));

        }

        @Test
        public void description_summary_works() throws Exception {

            // given
            reversedItem.setDescription("Reversed item description");
            reversal.setDescription("REVERSAL of Reversed item description");
            regularItem1.setDescription("First description");
            regularItem2.setDescription("Second description");

            //when, then
            assertThat(invoice.getDescriptionSummary()).isEqualTo("First description | Second description");

        }

        @Test
        public void project_summary_works() throws Exception {

            // given
            Project project1 = new Project();
            project1.setName("Pr1");
            Project project2 = new Project();
            project2.setName("Pr2");
            reversedItem.setProject(project1);
            regularItem1.setProject(project2);
            regularItem2.setProject(project2);

            // when, then
            assertThat(invoice.getProjectSummary()).isEqualTo("Pr2");

            // and when
            regularItem2.setProject(project1);

            // then
            assertThat(invoice.getProjectSummary()).isEqualTo("Pr2 | Pr1");

        }

        @Test
        public void property_summary_works() throws Exception {

            // given
            Property prop1 = new Property();
            prop1.setName("Prop1");
            Property prop2 = new Property();
            prop2.setName("Prop2");
            reversedItem.setFixedAsset(prop1);
            regularItem1.setFixedAsset(prop2);
            regularItem2.setFixedAsset(prop2);

            // when, then
            assertThat(invoice.getPropertySummary()).isEqualTo("Prop2");

            // and when
            regularItem2.setFixedAsset(prop1);

            // then
            assertThat(invoice.getPropertySummary()).isEqualTo("Prop2 | Prop1");

        }

    }

    public static class Notification_Test extends IncomingInvoice_Test {

        @Mock
        InvoiceRepository mockInvoiceRepository;

        @Mock
        OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

        @Test
        public void historicalPaymentMethod_works() throws Exception {
            String notification;

            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice() {
                public String doubleInvoiceCheck() {
                    return null;
                }

                public String buyerBarcodeMatchValidation() {
                    return null;
                }
            };
            Party seller = new Organisation();
            incomingInvoice.setSeller(seller);
            incomingInvoice.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
            incomingInvoice.invoiceRepository = mockInvoiceRepository;

            IncomingInvoice incomingInvoice1 = new IncomingInvoice();
            incomingInvoice1.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            IncomingInvoice incomingInvoice2 = new IncomingInvoice();
            incomingInvoice2.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
            IncomingInvoice incomingInvoice3 = new IncomingInvoice();
            incomingInvoice3.setPaymentMethod(PaymentMethod.CASH);
            IncomingInvoice incomingInvoice4 = new IncomingInvoice();
            incomingInvoice4.setPaymentMethod(null);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockInvoiceRepository).findBySeller(seller);
                will(returnValue(Arrays.asList(incomingInvoice1, incomingInvoice2, incomingInvoice3, incomingInvoice4)));
            }});

            // when
            notification = incomingInvoice.getNotification();

            // then
            assertThat(notification).isEqualTo("WARNING: payment method is set to bank transfer, but previous invoices from this seller have used the following payment methods: Direct Debit, Cash ");

            // and expecting
            context.checking(new Expectations() {{
                oneOf(mockInvoiceRepository).findBySeller(seller);
                will(returnValue(Arrays.asList(incomingInvoice2))); // All historical invoices use payment method BANK_TRANSFER...
            }});

            // when
            notification = incomingInvoice.getNotification();

            // then
            assertThat(notification).isNull(); // ... so no warning
        }

        @Test
        public void buyerBarcodeMatchValidation_works() {

            String notification;

            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice();
            Party buyerDerived = new Organisation();
            Party buyerOnInvoice = new Organisation();
            incomingInvoice.buyerFinder = new BuyerFinder() {
                @Override
                public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice) {
                    return buyerDerived;
                }
            };

            incomingInvoice.setBuyer(buyerOnInvoice);

            // when
            notification = incomingInvoice.getNotification();

            // then
            assertThat(notification).isEqualTo("Buyer does not match barcode (document name); ");

            // and given (buyers matching)
            incomingInvoice.setBuyer(buyerDerived);
            // when
            notification = incomingInvoice.getNotification();
            // then
            assertThat(notification).isNull();

            // and given (no buyer derived)
            incomingInvoice.buyerFinder = new BuyerFinder() {
                @Override
                public Party buyerDerivedFromDocumentName(final IncomingInvoice incomingInvoice) {
                    return null;
                }
            };

            // when
            notification = incomingInvoice.getNotification();

            // then
            assertThat(notification).isNull();
        }

        @Test
        public void mismatchedItemTypes_works() {

            String notification;

            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice() {
                public String doubleInvoiceCheck() {
                    return null;
                }

                public String buyerBarcodeMatchValidation() {
                    return null;
                }

                public String paymentMethodValidation() {
                    return null;
                }

                protected void invalidateApproval() {
                }
            };

            IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
            invoiceItem.setInvoice(incomingInvoice);
            invoiceItem.setIncomingInvoiceType(IncomingInvoiceType.PROPERTY_EXPENSES);
            incomingInvoice.setItems(Sets.newTreeSet(Collections.singletonList(invoiceItem)));
            incomingInvoice.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

            OrderItem orderItem = new OrderItem();
            Order order = new Order();
            order.setType(IncomingInvoiceType.CAPEX);
            orderItem.setOrdr(order);
            OrderItemInvoiceItemLink link = new OrderItemInvoiceItemLink();
            link.setOrderItem(orderItem);
            link.setInvoiceItem(invoiceItem);
            Optional<OrderItemInvoiceItemLink> optional = Optional.of(link);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
                will(returnValue(optional));
            }});

            // when
            notification = incomingInvoice.getNotification();

            // then
            assertThat(notification).isEqualTo("WARNING: mismatched types between linked items: an invoice item of type PROPERTY_EXPENSES is linked to an order item of type CAPEX");
        }

    }

    public static class CompleteInvoiceAndItem_Test extends IncomingInvoice_Test {

        @Mock
        StateTransitionService mockStateTransitionService;

        @Mock
        OrderItemRepository mockOrderItemRepository;

        @Mock
        PartyRoleRepository mockPartyRoleRepository;

        @Mock
        OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

        @Test
        public void completeInvoice_and_completeInvoiceItem_works() {
            // given
            IncomingInvoice incomingInvoice = new IncomingInvoice();
            IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
            invoiceItem.setInvoice(incomingInvoice);
            invoiceItem.setIncomingInvoiceType(IncomingInvoiceType.CAPEX);
            incomingInvoice.getItems().add(invoiceItem);

            incomingInvoice.stateTransitionService = mockStateTransitionService;
            incomingInvoice.orderItemRepository = mockOrderItemRepository;
            incomingInvoice.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
            incomingInvoice.partyRoleRepository = mockPartyRoleRepository;

            IncomingInvoiceType newIncomingInvoiceType = IncomingInvoiceType.CAPEX;
            Organisation newSeller = new Organisation();
            BankAccount newBankAccount = new BankAccount();
            String newInvoiceNumber = "1234";
            LocalDate newDateReceived = new LocalDate();
            LocalDate newInvoiceDate = new LocalDate();
            LocalDate newDueDate = new LocalDate();
            PaymentMethod newPaymentMethod = PaymentMethod.DIRECT_DEBIT;
            Currency newCurrency = new Currency();
            Order order = new Order();
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setOrdr(order);
            String newDescription = "Description";
            BigDecimal newNetAmount = BigDecimal.ONE;
            BigDecimal newVatAmount = BigDecimal.ONE;
            Tax newTax = new Tax();
            BigDecimal newGrossAmount = BigDecimal.ONE;
            Charge newCharge = new Charge();
            newOrderItem.setCharge(newCharge);
            String newPeriod = "F2019";

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockPartyRoleRepository).findOrCreate(newSeller, IncomingInvoiceRoleTypeEnum.SUPPLIER);
                oneOf(mockStateTransitionService).trigger(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, null, null, null);
            }});

            // when
            incomingInvoice.completeInvoice(
                    newIncomingInvoiceType,
                    newSeller,
                    Boolean.TRUE,
                    newBankAccount,
                    newInvoiceNumber,
                    newDateReceived,
                    newInvoiceDate,
                    newDueDate,
                    newPaymentMethod,
                    newCurrency);

            // then
            assertThat(incomingInvoice.getType()).isEqualTo(newIncomingInvoiceType);
            assertThat(incomingInvoice.getSeller()).isEqualTo(newSeller);
            assertThat(incomingInvoice.getBankAccount()).isEqualTo(newBankAccount);
            assertThat(incomingInvoice.getInvoiceNumber()).isEqualTo(newInvoiceNumber);
            assertThat(incomingInvoice.getDateReceived()).isEqualTo(newDateReceived);
            assertThat(incomingInvoice.getInvoiceDate()).isEqualTo(newInvoiceDate);
            assertThat(incomingInvoice.getDueDate()).isEqualTo(newDueDate);
            assertThat(incomingInvoice.getPaymentMethod()).isEqualTo(newPaymentMethod);
            assertThat(incomingInvoice.getCurrency()).isEqualTo(newCurrency);

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockOrderItemRepository).findUnique(order, newCharge, 0);
                will(returnValue(newOrderItem));
                oneOf(mockOrderItemInvoiceItemLinkRepository).findOrCreateLink(newOrderItem, invoiceItem, BigDecimal.ONE);
            }});

            //when
            incomingInvoice.completeInvoiceItem(
                    newOrderItem,
                    newDescription,
                    newNetAmount,
                    newVatAmount,
                    newTax,
                    newGrossAmount,
                    newCharge,
                    newPeriod);

            assertThat(incomingInvoice.getDescriptionSummary()).isEqualTo(newDescription);
            assertThat(incomingInvoice.getItems().first().getDescription()).isEqualTo(newDescription);
            assertThat(incomingInvoice.getItems().first().getNetAmount()).isEqualTo(newNetAmount);
            assertThat(incomingInvoice.getItems().first().getVatAmount()).isEqualTo(newVatAmount);
            assertThat(incomingInvoice.getItems().first().getTax()).isEqualTo(newTax);
            assertThat(incomingInvoice.getItems().first().getGrossAmount()).isEqualTo(newGrossAmount);
            assertThat(incomingInvoice.getItems().first().getCharge()).isEqualTo(newCharge);
        }

    }

}