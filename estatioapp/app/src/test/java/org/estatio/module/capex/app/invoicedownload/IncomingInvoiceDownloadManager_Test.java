package org.estatio.module.capex.app.invoicedownload;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceDownloadManager_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock IncomingInvoiceItemRepository mockIncomingInvoiceItemRepository;

    @Test
    public void getReportedInvoiceItemsWithPropertyForPeriod_works() throws Exception {

        // given
        IncomingInvoiceDownloadManager manager = new IncomingInvoiceDownloadManager();
        manager.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;
        LocalDate startDate = new LocalDate(2017,1,1);
        LocalDate endDate = new LocalDate(2017, 1, 30);
        List<LocalDate> distinctLocalDates =
                Arrays.asList(
                        new LocalDate(2016, 12, 31),
                        new LocalDate(2017, 1, 2),
                        new LocalDate(2017, 1, 1),
                        new LocalDate(2017, 1, 30),
                        new LocalDate(2017, 1, 31));
        IncomingInvoiceItem item1 = new IncomingInvoiceItem(){
            @Override public FixedAsset getFixedAsset() {
                return new Property();
            }
        };
        IncomingInvoiceItem item2 = new IncomingInvoiceItem(){
            @Override public FixedAsset getFixedAsset() {
                return new Property();
            }
        };
        IncomingInvoiceItem item3 = new IncomingInvoiceItem();

        // expect
        context.checking(new Expectations(){{
            allowing(mockIncomingInvoiceItemRepository).findDistinctReportDates();
            will(returnValue(distinctLocalDates));
            oneOf(mockIncomingInvoiceItemRepository).findCompletedOrLaterByReportedDate(new LocalDate(2017,1,2));
            will(returnValue(Arrays.asList(item1, item2)));
            oneOf(mockIncomingInvoiceItemRepository).findCompletedOrLaterByReportedDate(startDate);
            will(returnValue(Arrays.asList(item3)));
            oneOf(mockIncomingInvoiceItemRepository).findCompletedOrLaterByReportedDate(endDate);
            will(returnValue(Lists.emptyList()));
        }});

        // when
        List<IncomingInvoiceItem> result = manager.getReportedInvoiceItemsWithPropertyForPeriod(startDate, endDate);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).doesNotContain(item3);

    }

}