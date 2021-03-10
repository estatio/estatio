package org.estatio.module.capex.contributions;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;

@Mixin
public class IncomingInvoice_verifySupplier {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_verifySupplier(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public IncomingInvoice $$( final OrganisationNameNumberViewModel organisationCheck) {
        Organisation orgToVerify = (Organisation) incomingInvoice.getSeller();
        orgToVerify.verify(organisationCheck);
        return incomingInvoice;
    }

    public List<OrganisationNameNumberViewModel> choices0$$(){
        Organisation orgToVerify = (Organisation) incomingInvoice.getSeller();
        return orgToVerify.choices0Verify();
    }

    public boolean hide$$(){
        Organisation orgToVerify = (Organisation) incomingInvoice.getSeller();
        return orgToVerify==null || orgToVerify.isVerified();
    }

}
