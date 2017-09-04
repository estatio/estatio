package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceItemRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    private IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();

    @Mock
    private IncomingInvoice mockInvoice;
    @Mock
    private IncomingInvoice stubInvoice2;

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;


    @Mock
    private Charge mockCharge;

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {{
            ignoring(mockInvoice).invalidateApproval();
            ignoring(stubInvoice2).invalidateApproval();
        }});
    }

    @Test
    public void upsert_works() throws Exception {


        // given
        IncomingInvoiceItemRepository incomingInvoiceItemRepository = new IncomingInvoiceItemRepository(){
            @Override
            public IncomingInvoiceItem findByInvoiceAndChargeAndSequence(final IncomingInvoice invoice, final Charge charge, final BigInteger sequence) {
                return invoiceItem;
            }

        };
        BigInteger sequence = new BigInteger("1");
        String description = "";
        Tax tax = new Tax();
        LocalDate dueDate = new LocalDate(2017,1,1);
        LocalDate startDate = new LocalDate(2017,1,2);
        LocalDate endDate = new LocalDate(2017,1, 3);
        Property property = new Property();
        Project project = new Project();
        BudgetItem budgetItem = new BudgetItem();
        IncomingInvoiceType type = IncomingInvoiceType.CORPORATE_EXPENSES;
        invoiceItem.setInvoice(mockInvoice);

        assertThat(invoiceItem.getInvoice()).isEqualTo(mockInvoice);
        assertThat(invoiceItem.getIncomingInvoiceType()).isNull();
        assertThat(invoiceItem.getCharge()).isNull();
        assertThat(invoiceItem.getSequence()).isNull();
        assertThat(invoiceItem.getDescription()).isNull();
        assertThat(invoiceItem.getNetAmount()).isNull();
        assertThat(invoiceItem.getVatAmount()).isNull();
        assertThat(invoiceItem.getGrossAmount()).isNull();
        assertThat(invoiceItem.getTax()).isNull();
        assertThat(invoiceItem.getDueDate()).isNull();
        assertThat(invoiceItem.getStartDate()).isNull();
        assertThat(invoiceItem.getEndDate()).isNull();
        assertThat(invoiceItem.getFixedAsset()).isNull();
        assertThat(invoiceItem.getProject()).isNull();

        // expect
        context.checking(new Expectations(){
            {
                oneOf(mockInvoice).recalculateAmounts();
            }
        });

        // when
        incomingInvoiceItemRepository.upsert(
                sequence,
                mockInvoice,
                type,
                mockCharge,
                description,
                BigDecimal.ZERO,
                BigDecimal.ONE,
                BigDecimal.TEN,
                tax,
                dueDate,
                startDate,
                endDate,
                property,
                project,
                budgetItem);

        // then
        assertThat(invoiceItem.getInvoice()).isEqualTo(mockInvoice); // should not updated
        assertThat(invoiceItem.getCharge()).isNull(); // should not updated
        assertThat(invoiceItem.getIncomingInvoiceType()).isNull(); // should not be updated
        assertThat(invoiceItem.getSequence()).isEqualTo(sequence);
        assertThat(invoiceItem.getDescription()).isEqualTo(description);
        assertThat(invoiceItem.getNetAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(invoiceItem.getVatAmount()).isEqualTo(BigDecimal.ONE);
        assertThat(invoiceItem.getGrossAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(invoiceItem.getTax()).isEqualTo(tax);
        assertThat(invoiceItem.getDueDate()).isEqualTo(dueDate);
        assertThat(invoiceItem.getStartDate()).isEqualTo(startDate);
        assertThat(invoiceItem.getEndDate()).isEqualTo(endDate);
        assertThat(invoiceItem.getFixedAsset()).isEqualTo(property);
        assertThat(invoiceItem.getProject()).isEqualTo(project);

    }

    @Test
    public void mergeItems_works() throws Exception {

        // given
        IncomingInvoiceItemRepository incomingInvoiceItemRepository = new IncomingInvoiceItemRepository();
        IncomingInvoiceItem sourceItem = new IncomingInvoiceItem();
        sourceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        sourceItem.repositoryService = mockRepositoryService;
        sourceItem.setInvoice(mockInvoice);

        IncomingInvoiceItem targetItem = new IncomingInvoiceItem();
        targetItem.setInvoice(stubInvoice2);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(sourceItem);
            will(returnValue(new ArrayList<>()));
            oneOf(mockRepositoryService).removeAndFlush(sourceItem);
            oneOf(mockInvoice).recalculateAmounts();
        }});

        // when
        sourceItem.setNetAmount(new BigDecimal("10"));
        targetItem.setNetAmount(new BigDecimal("10.10"));
        incomingInvoiceItemRepository.mergeItems(sourceItem, targetItem);

        // assert
        /**
        actually superfluous;
        {@link IncomingInvoiceItem#addAmounts(BigDecimal, BigDecimal, BigDecimal)} separately tested
         */
        Assertions.assertThat(targetItem.getNetAmount()).isEqualTo(new BigDecimal("20.10"));

    }
    
}