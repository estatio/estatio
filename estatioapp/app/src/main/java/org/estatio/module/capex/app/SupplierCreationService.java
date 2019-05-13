package org.estatio.module.capex.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class SupplierCreationService {

    public List<OrganisationNameNumberViewModel> autoCompleteNewSupplier(
            @MinLength(3) final String search,
            final String atPath) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // nothing
        }
        List<OrganisationNameNumberViewModel> result = new ArrayList<>(chamberOfCommerceCodeLookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(search, atPath));
        result.add(new OrganisationNameNumberViewModel(search, null));
        return result;
    }

    public Organisation createNewSupplierAndOptionallyBankAccount(
            final OrganisationNameNumberViewModel newSupplierCandidate,
            final Country newSupplierCountry,
            final String newSupplierIban) {
        Organisation organisation = organisationRepository.newOrganisation(null, true, newSupplierCandidate.getOrganisationName(), newSupplierCountry);
        partyRoleRepository.findOrCreate(organisation, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        if (newSupplierCandidate.getChamberOfCommerceCode() != null)
            organisation.setChamberOfCommerceCode(newSupplierCandidate.getChamberOfCommerceCode());

        if (newSupplierIban != null) {
            bankAccountRepository.newBankAccount(organisation, newSupplierIban, null);
        }

        return organisation;
    }

    @Inject
    private ChamberOfCommerceCodeLookUpService chamberOfCommerceCodeLookUpService;

    @Inject
    private OrganisationRepository organisationRepository;

    @Inject
    private PartyRoleRepository partyRoleRepository;

    @Inject
    private BankAccountRepository bankAccountRepository;
}
