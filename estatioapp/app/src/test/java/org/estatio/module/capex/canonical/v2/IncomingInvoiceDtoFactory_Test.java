package org.estatio.module.capex.canonical.v2;

import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.dto.DtoMappingHelper;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.canonical.incominginvoice.v2.IncomingInvoiceDto;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.invoice.dom.InvoiceItem;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceDtoFactory_Test {

    private IncomingInvoice incomingInvoice;
    IncomingInvoiceItem notReversedItem1;
    IncomingInvoiceItem notReversedItem2;
    IncomingInvoiceItem reversedItem3;
    IncomingInvoiceItem reversalItem4;
    IncomingInvoiceItem reversedItem5;
    IncomingInvoiceItem reversalItem6;
    IncomingInvoiceItem notReversedItem7;
    IncomingInvoiceItem notReversedItem8;

    @Before
    public void setUp() throws Exception {
        incomingInvoice = new IncomingInvoice();

        final TreeSet<InvoiceItem> items = new TreeSet<>();
        incomingInvoice.setItems(items);

        notReversedItem1 = addItem(incomingInvoice, "item1");
        notReversedItem2 = addItem(incomingInvoice, "item2");

        reversedItem3 = addItem(incomingInvoice, "item3");
        reversalItem4 = addItem(incomingInvoice, "item4");
        reversalItem4.setReversalOf(reversedItem3);
    }

    private static IncomingInvoiceItem addItem(final IncomingInvoice incomingInvoice, final String str) {
        final IncomingInvoiceItem item = new IncomingInvoiceItem() {
            @Override public String toString() {
                return str;
            }

            @Override public int compareTo(final IncomingInvoiceItem other) {
                return toString().compareTo(other.toString());
            }
        };
        item.setInvoice(incomingInvoice);
        incomingInvoice.getItems().add(item);
        return item;
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    @Mock DtoMappingHelper mockDtoMappingHelper;

    @Mock CodaDocHeadRepository mockCodaDocHeadRepository;

    @Test
    @Ignore // TODO: maybe finish... ?
    public void items_reversal_status_test() throws Exception {

        // given
        IncomingInvoiceDtoFactory factory = new IncomingInvoiceDtoFactory();
        factory.mappingHelper = mockDtoMappingHelper;
        factory.codaDocHeadRepository = mockCodaDocHeadRepository;

        // expect
        context.checking(new Expectations(){{
            allowing(mockDtoMappingHelper).oidDtoFor(incomingInvoice);
            allowing(mockCodaDocHeadRepository).findByIncomingInvoice(incomingInvoice);
            will(returnValue(new CodaDocHead()));
        }});
        // when
        final IncomingInvoiceDto incomingInvoiceDto = factory.newDto(incomingInvoice);
        // then
        assertThat(incomingInvoiceDto.getItems()).hasSize(4);
        // todo: test reversal status
        
    }


}