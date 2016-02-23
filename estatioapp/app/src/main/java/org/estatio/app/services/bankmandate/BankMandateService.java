package org.estatio.app.services.bankmandate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.SequenceType;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(named = "Accounts")
public class BankMandateService extends UdoDomainService<BankMandateService>{

    public BankMandateService() {
        super(BankMandateService.class);
    }

    public List<BankMandate> findBankMandatesForUpdate(){
        List<BankMandate> mandatesForUpdate = new ArrayList<>();
        for (Invoice invoice : invoiceRepository.findByStatus(InvoiceStatus.INVOICED)) {
            BankMandate mandate = invoice.getPaidBy();
            if (mandate != null && mandate.getSequenceType() == SequenceType.FIRST) {
                if (!mandatesForUpdate.contains(mandate)) {
                    mandatesForUpdate.add(mandate);
                }
            }
        }
        return mandatesForUpdate;
    }

    @Inject
    protected Invoices invoiceRepository;
}
