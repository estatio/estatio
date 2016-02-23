package org.estatio.app.services.bankmandate;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.assertj.core.api.Assertions;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.SequenceType;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class BankMandateServiceTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Invoices invoiceRepository;

    BankMandateService bankMandateService;
    BankMandate bankMandate1;
    BankMandate bankMandate2;
    List<Invoice> invoices;

    @Before
    public void setup() {
        bankMandateService = new BankMandateService();
        bankMandateService.invoiceRepository = invoiceRepository;
        bankMandate1 = new BankMandate();
        bankMandate1.setSequenceType(SequenceType.FIRST);
        bankMandate2 = new BankMandate();
        bankMandate2.setSequenceType(SequenceType.RECURRENT);
        Invoice invoice1 = new Invoice();
        invoice1.setPaidBy(bankMandate1);
        Invoice invoice2 = new Invoice();
        invoice2.setPaidBy(bankMandate1);
        Invoice invoice3 = new Invoice();
        invoice3.setPaidBy(bankMandate2);
        invoices = Arrays.asList(invoice1, invoice2, invoice3);
    }

    @Test
    public void findBankMandatesForUpdateTest() {

        // expect
        context.checking(new Expectations(){
            {
                oneOf(invoiceRepository).findByStatus(InvoiceStatus.INVOICED);
                will(returnValue(invoices));
            }

        });

        //when
        List<BankMandate> mandatesFound = bankMandateService.findBankMandatesForUpdate();

        //then
        Assertions.assertThat(mandatesFound.size()).isEqualTo(1);
        Assertions.assertThat(mandatesFound.get(0)).isEqualTo(bankMandate1);

    }


}
