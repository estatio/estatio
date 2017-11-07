package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.metamodel.MetaModelService2;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.bankaccount.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.tax.dom.Tax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.COMPLETED;
import static org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState.NEW;
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
            invoice.setBankAccount(new BankAccount());

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
            invoice.setSeller(new Organisation());
            invoice.setBankAccount(new BankAccount());
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
            IncomingInvoiceItem item = new IncomingInvoiceItem(){
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
    }




    public static class splitItem_Test extends IncomingInvoice_Test {

        @Test
        public void happy_case() throws Exception {

            // given
            invoice.setType(IncomingInvoiceType.CAPEX);
            invoice.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;

            LocalDate dueDate = new LocalDate(2018,01,01);
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
            context.checking(new Expectations(){{
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
            invoice.splitItem(itemToSplit, description, newItemNetAmount, newItemVatAmount, tax, newItemGrossAmount,charge, property, project, budgetItem, period);

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
            context.checking(new Expectations(){{
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
                @Override public IncomingInvoiceApprovalState getApprovalState() {
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
            IncomingInvoiceItem item = new IncomingInvoiceItem(){
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

}