package org.estatio.module.capex.dom.invoice;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.base.platform.applib.ReasonBuffer2;
import org.estatio.module.capex.dom.codalink.CodaDocLink;
import org.estatio.module.capex.dom.codalink.CodaDocLinkRepository;
import org.estatio.module.invoice.dom.InvoiceItem;

public class IncomingInvoice_appendReasonReverseDisabledIfAny_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    CodaDocLinkRepository mockCodaDocLinkRepository;

    private IncomingInvoice incomingInvoice;
    private ReasonBuffer2 buf;
    private SortedSet<InvoiceItem> items;

    @Before
    public void setUp() throws Exception {
        incomingInvoice = new IncomingInvoice();
        incomingInvoice.codaDocLinkRepository = mockCodaDocLinkRepository;

        buf = ReasonBuffer2.forAll();

        items = new TreeSet<>();
        incomingInvoice.setItems(items);
    }

    @Test
    public void no_items_to_reverse() throws Exception {
        // given
        Assertions.assertThat(items).isEmpty();

        // when
        incomingInvoice.appendReasonReverseDisabledIfAny(buf);

        // then
        final String reason = buf.getReason();
        Assertions.assertThat(reason).isNull();
    }

    @Test
    public void happy_case_when_there_are_some_items_to_reverse() throws Exception {

        // given
        items.add(new IncomingInvoiceItem(){
            @Override public boolean isReported() {
                return true;
            }

            @Override public boolean isReversal() {
                return false;
            }
        });

        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocLinkRepository).findByInvoice(incomingInvoice);
            will(returnValue(Collections.singletonList(new CodaDocLink())));
        }});

        // when
        incomingInvoice.appendReasonReverseDisabledIfAny(buf);

        // then
        Assertions.assertThat(buf.getReason()).isNull();

    }

    @Test
    public void some_items_to_reverse_but_previous_sync_failed() throws Exception {

        // given
        items.add(new IncomingInvoiceItem(){
            @Override public boolean isReported() {
                return true;
            }

            @Override public boolean isReversal() {
                return false;
            }
        });

        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocLinkRepository).findByInvoice(incomingInvoice);
            will(returnValue(Collections.emptyList()));
        }});

        // when
        incomingInvoice.appendReasonReverseDisabledIfAny(buf);

        // then
        Assertions.assertThat(buf.getReason()).isEqualTo("previous reporting/sync to CODA may have failed");


    }

}