package org.estatio.module.capex.dom.order;

import java.math.BigInteger;
import java.util.List;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.app.NumeratorForOrdersRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRepository_Test {

    private Order order = new Order();

    @Test
    public void upsert_when_already_exists() throws Exception {

        // given
        OrderRepository orderRepository = new OrderRepository() {
            @Override
            public Order findByOrderNumber(final String orderNumber) {
                return order;
            }
        };
        String number = "some number";
        String sellerOrderReference = "ref";
        LocalDate entryDate = new LocalDate(2017, 1, 1);
        LocalDate orderDate = new LocalDate(2017, 1, 2);
        Party seller = new Organisation();
        Party buyer = new Organisation();
        Property property = new Property();
        String atPath = "atPath";
        OrderApprovalState approvalState = OrderApprovalState.APPROVED;

        assertThat(order.getOrderNumber()).isNull();

        // when
        orderRepository.upsert(
                property,
                null,
                number,
                sellerOrderReference,
                entryDate,
                orderDate,
                seller,
                buyer,
                atPath,
                approvalState);

        // then
        assertThat(order.getOrderNumber()).isNull();
        assertThat(order.getSellerOrderReference()).isEqualTo(sellerOrderReference);
        assertThat(order.getEntryDate()).isEqualTo(entryDate);
        assertThat(order.getOrderDate()).isEqualTo(orderDate);
        assertThat(order.getSeller()).isEqualTo(seller);
        assertThat(order.getBuyer()).isEqualTo(buyer);
        assertThat(order.getProperty()).isEqualTo(property);
        assertThat(order.getAtPath()).isEqualTo(atPath);
        assertThat(order.getApprovalState()).isNull(); // is ignored.

    }

    @Test
    public void strip_ita_references_for_order_number() throws Exception {
        // given
        final String nextIncrement = "0005";
        final Property property = new Property();
        property.setReference("RON");
        final Project project = new Project();
        project.setReference("ITPR001");
        final Charge charge = new Charge();
        charge.setReference("ITWT002");

        // when
        final String orderNumber = OrderRepository.toItaOrderNumber(nextIncrement, property, null, project, charge);

        // then
        assertThat(orderNumber).isEqualTo("0005/RON/001/002");
    }

    @Test
    public void to_ita_ordernumber_works() {

        // given
        OrderRepository orderRepository = new OrderRepository();
        String nextIncrement = "1234";

        // when
        Property property = new Property();
        property.setReference("OXF");
        Project project = new Project();
        project.setReference("PR123");
        Charge charge = new Charge();
        charge.setReference("N005");

        // then
        assertThat(orderRepository.toItaOrderNumber(nextIncrement, property, null, project, charge)).isEqualTo("1234/OXF/123/005");
        assertThat(orderRepository.toItaOrderNumber(nextIncrement, property, null, null, charge)).isEqualTo("1234/OXF//005");
        assertThat(orderRepository.toItaOrderNumber(nextIncrement, property, null, project, null)).isEqualTo("1234/OXF/123/");
        assertThat(orderRepository.toItaOrderNumber(nextIncrement, property, null, null, null)).isEqualTo("1234/OXF//");

        // when
        String multiPropertyRef = "GEN";
        assertThat(orderRepository.toItaOrderNumber(nextIncrement, null, multiPropertyRef, project, charge)).isEqualTo("1234/GEN/123/005");

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    NumeratorForOrdersRepository mockNumeratorForOrdersRepository;

    @Test
    public void generateNextOrderNumber_skips_when_used() throws Exception {
        // given
        final Organisation buyerParty = new Organisation();

        OrderRepository orderRepository = new OrderRepository() {
            // hacky way of mocking existing orders with future increments of the numerator in the order number
            @Programmatic
            public List<Order> findByBuyerAndBuyerOrderNumber(
                    final Organisation buyer,
                    final BigInteger buyerOrderNumber) {
                return buyer.equals(buyerParty) && (buyerOrderNumber.equals(new BigInteger("2")) || buyerOrderNumber.equals(new BigInteger("3"))) ? Lists.newArrayList(order) : Lists.emptyList();
            }
        };

        orderRepository.numeratorForOrdersRepository = mockNumeratorForOrdersRepository;

        final Numerator numerator = new Numerator();
        numerator.setLastIncrement(new BigInteger("0"));
        numerator.setFormat("%04d");

        // expecting
        context.checking(new Expectations() {{
            // first generate call
            oneOf(mockNumeratorForOrdersRepository).findOrCreateNumerator("/ITA", buyerParty, "%04d");
            will(returnValue(numerator));

            // second generate call
            oneOf(mockNumeratorForOrdersRepository).findOrCreateNumerator("/ITA", buyerParty, "%04d");
            will(returnValue(numerator));
        }});

        // when
        final String nextIncrementIsAvailable = orderRepository.generateNextOrderNumber(buyerParty, "/ITA");
        final String skipsOneIncrement = orderRepository.generateNextOrderNumber(buyerParty, "/ITA");

        // then
        assertThat(nextIncrementIsAvailable).isEqualTo("0001"); // first increment is happily available
        assertThat(skipsOneIncrement).isEqualTo("0004"); // but orders with the next two increments already exist
    }

    @Test
    public void generateNextOrderNumber_works_for_nonItalian() throws Exception {
        // given
        final Organisation buyerParty = new Organisation();
        final String atPath = "/BEL";

        OrderRepository orderRepository = new OrderRepository();
        orderRepository.numeratorForOrdersRepository = mockNumeratorForOrdersRepository;

        final Numerator numerator = new Numerator();
        numerator.setLastIncrement(new BigInteger("0"));
        numerator.setFormat("BE%05d");

        // expecting
        context.checking(new Expectations() {{
            // first generate call
            oneOf(mockNumeratorForOrdersRepository).findOrCreateNumerator(atPath, null, "%05d");
            will(returnValue(numerator));
        }});

        // when
        final String nextIncrementIsAvailable = orderRepository.generateNextOrderNumber(buyerParty, atPath);

        // then
        assertThat(nextIncrementIsAvailable).isEqualTo("BE00001");
    }

}