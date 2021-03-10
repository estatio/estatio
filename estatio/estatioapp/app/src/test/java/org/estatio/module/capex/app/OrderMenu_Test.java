package org.estatio.module.capex.app;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;

public class OrderMenu_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderRepository mockOrderRepository;

    @Mock
    PartyRepository mockPartyRepository;

    @Test
    public void filterOrFindByDocumentName_find_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockOrderRepository).findOrderByDocumentName("123");
        }});

        // when
        builder.filterOrFindByDocumentName("123");
    }

    @Test
    public void filterOrFindByDocumentName_filter_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);
        Order order1 = new Order();
        Order order2 = new Order();
        builder.setResult(Arrays.asList(order1, order2));
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockOrderRepository).findOrderByDocumentName("123");
            will(returnValue(Arrays.asList(order1)));
        }});

        // when
        builder.filterOrFindByDocumentName("123");

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(1);

    }

    @Test
    public void filterOrFindByDocumentName_with_null_works() {

        OrderMenu.OrderFinder builder;

        // and given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);

        // when
        List<Order> result = builder.filterOrFindByDocumentName(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();

    }

    @Test
    public void filterOrFindBySeller_find_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);
        Organisation seller = new Organisation();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPartyRepository).findParties("*abc*");
            will(returnValue(Arrays.asList(seller)));
            oneOf(mockOrderRepository).findBySeller(seller);
        }});

        // when
        builder.filterOrFindBySeller("abc");

    }

    @Test
    public void filterOrFindBySeller_filter_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);
        Order order1 = new Order();
        Person personToBeFiltered = new Person();
        Organisation seller = new Organisation();
        order1.setSeller(seller);
        Order order2 = new Order();
        builder.setResult(Arrays.asList(order1, order2));
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPartyRepository).findParties("*abc*");
            will(returnValue(Arrays.asList(seller, personToBeFiltered)));
        }});

        // when
        builder.filterOrFindBySeller("abc");

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(1);

    }

    @Test
    public void filterOrFindBySeller_with_null_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);

        // when
        List<Order> result = builder.filterOrFindBySeller(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();
    }


    @Test
    public void filterOrFindByOrderDate_find_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);
        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();
        order1.setOrderDate(new LocalDate(2017,7,15));
        order2.setOrderDate(new LocalDate(2017,7,5));
        order3.setOrderDate(new LocalDate(2017,7,16));

        final LocalDate dateSearchedFor =  new LocalDate(2017, 7,10);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderRepository).listAll();
            will(returnValue(Arrays.asList(order1, order2, order3)));
        }});

        // when
        builder.filterOrFindByOrderDate(dateSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);
        Assertions.assertThat(builder.getResult()).contains(order1);
        Assertions.assertThat(builder.getResult()).contains(order2);

    }

    @Test
    public void filterOrFindByOrderDate_filter_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);
        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();
        order1.setOrderDate(new LocalDate(2017,7,15));
        order2.setOrderDate(new LocalDate(2017,7,5));
        order3.setOrderDate(new LocalDate(2017,7,16));;

        builder.setResult(Arrays.asList(order1, order2, order3));

        final LocalDate dateSearchedFor =  new LocalDate(2017, 7,10);

        Assertions.assertThat(builder.getResult().size()).isEqualTo(3);

        // when
        builder.filterOrFindByOrderDate(dateSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);
        Assertions.assertThat(builder.getResult()).contains(order1);
        Assertions.assertThat(builder.getResult()).contains(order2);

    }

    @Test
    public void filterOrFindInvoiceDate_with_null_works() {

        OrderMenu.OrderFinder builder;

        // given
        builder = new OrderMenu.OrderFinder(mockOrderRepository, mockPartyRepository);

        // when
        List<Order> result = builder.filterOrFindByOrderDate(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();
    }

}