package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;

public class IncomingInvoiceMenu_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceRepository mockIncomingInvoiceRepository;

    @Mock
    InvoiceRepository mockInvoiceRepository;

    @Mock
    PartyRepository mockPartyRepository;

    @Test
    public void filterOrFindByDocumentName_find_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockIncomingInvoiceRepository).findIncomingInvoiceByDocumentName("123");
        }});

        // when
        builder.filterOrFindByDocumentName("123");
    }

    @Test
    public void filterOrFindByDocumentName_filter_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        IncomingInvoice invoice2 = new IncomingInvoice();
        builder.setResult(Arrays.asList(invoice1, invoice2));
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockIncomingInvoiceRepository).findIncomingInvoiceByDocumentName("123");
            will(returnValue(Arrays.asList(invoice1)));
        }});

        // when
        builder.filterOrFindByDocumentName("123");

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(1);

    }

    @Test
    public void filterOrFindByDocumentName_with_null_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // and given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);

        // when
        List<IncomingInvoice> result = builder.filterOrFindByDocumentName(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();

    }

    @Test
    public void filterOrFindBySeller_find_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        Organisation seller = new Organisation();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPartyRepository).findParties("*abc*");
            will(returnValue(Arrays.asList(seller)));
            oneOf(mockInvoiceRepository).findBySeller(seller);
        }});

        // when
        builder.filterOrFindBySeller("abc");

    }

    @Test
    public void filterOrFindBySeller_filter_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        Person personToBeFiltered = new Person();
        Organisation seller = new Organisation();
        invoice1.setSeller(seller);
        IncomingInvoice invoice2 = new IncomingInvoice();
        builder.setResult(Arrays.asList(invoice1, invoice2));
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

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);

        // when
        List<IncomingInvoice> result = builder.filterOrFindBySeller(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void filterOrFindByGrossAmount_find_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        IncomingInvoice invoice2 = new IncomingInvoice();
        IncomingInvoice invoice3 = new IncomingInvoice();
        invoice1.setGrossAmount(new BigDecimal("9.50"));
        invoice2.setGrossAmount(new BigDecimal("10.50"));
        invoice3.setGrossAmount(new BigDecimal("10.51"));

        final BigDecimal amountSearchedFor = new BigDecimal("10.00");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).listAll();
            will(returnValue(Arrays.asList(invoice1, invoice2, invoice3)));
        }});

        // when
        builder.filterOrFindByGrossAmount(amountSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);
        Assertions.assertThat(builder.getResult()).contains(invoice1);
        Assertions.assertThat(builder.getResult()).contains(invoice2);

    }

    @Test
    public void filterOrFindByGrossAmount_negative_amounts_find_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        IncomingInvoice invoice2 = new IncomingInvoice();
        IncomingInvoice invoice3 = new IncomingInvoice();
        invoice1.setGrossAmount(new BigDecimal("-9.50"));
        invoice2.setGrossAmount(new BigDecimal("-10.50"));
        invoice3.setGrossAmount(new BigDecimal("-10.51"));

        final BigDecimal amountSearchedFor = new BigDecimal("-10.00");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).listAll();
            will(returnValue(Arrays.asList(invoice1, invoice2, invoice3)));
        }});

        // when
        builder.filterOrFindByGrossAmount(amountSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);
        Assertions.assertThat(builder.getResult()).contains(invoice1);
        Assertions.assertThat(builder.getResult()).contains(invoice2);

    }

    @Test
    public void filterOrFindByGrossAmount_filter_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        invoice1.setGrossAmount(new BigDecimal("9.50"));
        IncomingInvoice invoice2 = new IncomingInvoice();
        invoice2.setGrossAmount(new BigDecimal("9.49"));
        builder.setResult(Arrays.asList(invoice1, invoice2));

        final BigDecimal amountSearchedFor = new BigDecimal("10.00");

        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);

        // when
        builder.filterOrFindByGrossAmount(amountSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(1);
        Assertions.assertThat(builder.getResult()).contains(invoice1);

    }

    @Test
    public void filterOrFindByGrossAmount_negative_amounts_filter_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        invoice1.setGrossAmount(new BigDecimal("-9.50"));
        IncomingInvoice invoice2 = new IncomingInvoice();
        invoice2.setGrossAmount(new BigDecimal("-9.49"));
        builder.setResult(Arrays.asList(invoice1, invoice2));

        final BigDecimal amountSearchedFor = new BigDecimal("-10.00");

        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);

        // when
        builder.filterOrFindByGrossAmount(amountSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(1);
        Assertions.assertThat(builder.getResult()).contains(invoice1);

    }

    @Test
    public void filterOrFindByGrossAmount_with_null_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);

        // when
        List<IncomingInvoice> result = builder.filterOrFindByGrossAmount(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();

    }

    @Test
    public void filterOrFindInvoiceDate_find_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        IncomingInvoice invoice2 = new IncomingInvoice();
        IncomingInvoice invoice3 = new IncomingInvoice();
        invoice1.setInvoiceDate(new LocalDate(2017,7,15));
        invoice2.setInvoiceDate(new LocalDate(2017,7,5));
        invoice3.setInvoiceDate(new LocalDate(2017,7,16));

        final LocalDate dateSearchedFor =  new LocalDate(2017, 7,10);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceRepository).listAll();
            will(returnValue(Arrays.asList(invoice1, invoice2, invoice3)));
        }});

        // when
        builder.filterOrFindByInvoiceDate(dateSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);
        Assertions.assertThat(builder.getResult()).contains(invoice1);
        Assertions.assertThat(builder.getResult()).contains(invoice2);

    }

    @Test
    public void filterOrFindInvoiceDate_filter_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);
        IncomingInvoice invoice1 = new IncomingInvoice();
        IncomingInvoice invoice2 = new IncomingInvoice();
        IncomingInvoice invoice3 = new IncomingInvoice();
        invoice1.setInvoiceDate(new LocalDate(2017,7,15));
        invoice2.setInvoiceDate(new LocalDate(2017,7,5));
        invoice3.setInvoiceDate(new LocalDate(2017,7,16));

        builder.setResult(Arrays.asList(invoice1, invoice2, invoice3));

        final LocalDate dateSearchedFor =  new LocalDate(2017, 7,10);

        Assertions.assertThat(builder.getResult().size()).isEqualTo(3);

        // when
        builder.filterOrFindByInvoiceDate(dateSearchedFor);

        // then
        Assertions.assertThat(builder.getResult().size()).isEqualTo(2);
        Assertions.assertThat(builder.getResult()).contains(invoice1);
        Assertions.assertThat(builder.getResult()).contains(invoice2);

    }

    @Test
    public void filterOrFindInvoiceDate_with_null_works() {

        IncomingInvoiceMenu.IncomingInvoiceFinder builder;

        // given
        builder = new IncomingInvoiceMenu.IncomingInvoiceFinder(mockInvoiceRepository, mockIncomingInvoiceRepository, mockPartyRepository);

        // when
        List<IncomingInvoice> result = builder.filterOrFindByInvoiceDate(null).getResult();

        // then
        Assertions.assertThat(result).isEmpty();
    }

}