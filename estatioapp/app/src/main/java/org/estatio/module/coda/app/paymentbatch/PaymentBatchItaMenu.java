package org.estatio.module.coda.app.paymentbatch;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.application.app.CodaCmpCodeService;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "payments.PaymentBatchItaMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Payments",
        menuOrder = "70.4"
)
public class PaymentBatchItaMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public PaymentBatch importPaymentBatch(final Party epcBuyerCompany, final Blob spreadsheet){
        return paymentBatchItaUploadService.importPaymentBatch(epcBuyerCompany, spreadsheet);
    }

    public List<Party> choices0ImportPaymentBatch(){
        List<Party> result = new ArrayList<>();
        codaCmpCodeService.listAll().stream().forEach(ref->{
            Party p = partyRepository.findPartyByReference(ref);
            if (p!=null) result.add(p);
        });
        return result;
    }


    @Inject
    private PaymentBatchItaUploadService paymentBatchItaUploadService;

    @Inject
    private CodaCmpCodeService codaCmpCodeService;

    @Inject
    private PartyRepository partyRepository;

}
