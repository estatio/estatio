package org.estatio.module.party.contributions;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.app.document.IncomingDocViewModel;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;

@Mixin
public class IncomingDocViewModel_verifySupplier {

    private final IncomingDocViewModel incomingDocViewModel;

    public IncomingDocViewModel_verifySupplier(IncomingDocViewModel incomingDocViewModel) {
        this.incomingDocViewModel = incomingDocViewModel;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public IncomingDocViewModel $$( final OrganisationNameNumberViewModel organisationCheck) {
        Organisation orgToVerify = (Organisation) incomingDocViewModel.getSeller();
        orgToVerify.verify(organisationCheck);
        return incomingDocViewModel;
    }

    public List<OrganisationNameNumberViewModel> choices0$$(){
        Organisation orgToVerify = (Organisation) incomingDocViewModel.getSeller();
        return orgToVerify.choices0Verify();
    }

    public boolean hide$$(){
        Organisation orgToVerify = (Organisation) incomingDocViewModel.getSeller();
        return orgToVerify==null || orgToVerify.isVerified();
    }

}
