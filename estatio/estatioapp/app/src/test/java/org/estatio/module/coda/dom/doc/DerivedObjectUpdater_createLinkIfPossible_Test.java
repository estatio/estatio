package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.Optional;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;

public class DerivedObjectUpdater_createLinkIfPossible_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    private DerivedObjectUpdater derivedObjectUpdater;

    @Mock
    private Order mockOrder;
    @Mock
    private OrderItemInvoiceItemLinkRepository mockLinkRepository;

    private OrderItem stubOrderItem = new OrderItem();
    private IncomingInvoiceItem stubInvoiceItem = new IncomingInvoiceItem();
    private Charge stubCharge = new Charge();
    private Project stubProject = new Project();

    private BigDecimal nonZeroNetAmount = bd("101.23");

    @Before
    public void setUp() throws Exception {
        derivedObjectUpdater = new DerivedObjectUpdater();
        derivedObjectUpdater.linkRepository = mockLinkRepository;
    }

    @Test
    public void happy_case() throws Exception {

        // given
        context.checking(new Expectations() {{
            allowing(mockOrder).itemFor(stubCharge, stubProject);
            will(returnValue(Optional.of(stubOrderItem)));
        }});

        // expect
        context.checking(new Expectations() {{
            oneOf(mockLinkRepository).createLink(stubOrderItem, stubInvoiceItem, nonZeroNetAmount);
        }});

        // when
        derivedObjectUpdater.createLinkIfPossible(mockOrder, stubCharge, stubProject, stubInvoiceItem,
                nonZeroNetAmount);
    }

    @Test
    public void given_zero_amount() throws Exception {

        // given
        context.checking(new Expectations() {{
            allowing(mockOrder).itemFor(stubCharge, stubProject);
            will(returnValue(stubOrderItem));
        }});

        // expect
        context.checking(new Expectations() {{
            never(mockLinkRepository);
        }});

        // when
        derivedObjectUpdater.createLinkIfPossible(mockOrder, stubCharge, stubProject, stubInvoiceItem, bd("0.00"));
    }

    @Test
    public void given_null_order() throws Exception {

        // expect
        context.checking(new Expectations() {{
            never(mockOrder);
            never(mockLinkRepository);
        }});

        // when
        derivedObjectUpdater.createLinkIfPossible(null, stubCharge, stubProject, stubInvoiceItem,
                nonZeroNetAmount);
    }

    @Test
    public void given_null_charge() throws Exception {

        // expect
        context.checking(new Expectations() {{
            never(mockOrder);
            never(mockLinkRepository);
        }});

        // when
        derivedObjectUpdater.createLinkIfPossible(mockOrder, null, stubProject, stubInvoiceItem,
                nonZeroNetAmount);
    }


    @Test
    public void given_null_project() throws Exception {

        // expect
        context.checking(new Expectations() {{
            never(mockOrder);
            never(mockLinkRepository);
        }});

        // when
        derivedObjectUpdater.createLinkIfPossible(mockOrder, stubCharge, null, stubInvoiceItem,
                nonZeroNetAmount);
    }

    @Test
    public void given_no_item() throws Exception {

        // expect
        context.checking(new Expectations() {{
            allowing(mockOrder).itemFor(stubCharge, stubProject);
            will(returnValue(null));

            never(mockLinkRepository);
        }});

        // when
        derivedObjectUpdater.createLinkIfPossible(
                mockOrder, stubCharge, null, stubInvoiceItem, nonZeroNetAmount);
    }

    private static BigDecimal bd(final String val) {
        return new BigDecimal(val);
    }


}