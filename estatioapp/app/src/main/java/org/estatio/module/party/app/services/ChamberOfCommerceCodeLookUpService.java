package org.estatio.module.party.app.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.message.MessageService;

import org.estatio.module.party.app.services.siren.SirenResult;
import org.estatio.module.party.app.services.siren.SirenService;
import org.estatio.module.party.dom.Organisation;

@DomainService(
        objectType = "org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService",
        nature = NatureOfService.DOMAIN)
public class ChamberOfCommerceCodeLookUpService {

    private final static String connectionWarning = "A connection to the external Siren service could not be made";
    private final static String noResultsWarning = "A connection to the external Siren service could be made, but no results were returned";

    public List<OrganisationNameNumberViewModel> getChamberOfCommerceCodeCandidatesByOrganisation(final Organisation organisation) {
        return getChamberOfCommerceCodeCandidatesByOrganisation(organisation.getName(), organisation.getAtPath());
    }

    public List<OrganisationNameNumberViewModel> getChamberOfCommerceCodeCandidatesByOrganisation(final String name, final String atPath) {
        if (atPath == null)
            return Collections.emptyList();

        switch (atPath) {
            case "/FRA":
                return findCandidatesForFranceByName(name);
            //            return findCandidatesForFranceFake(name); // TODO: remove after developement

            default:
                return Collections.emptyList();
        }

    }

    public OrganisationNameNumberViewModel getChamberOfCommerceCodeCandidatesByCode(final Organisation organisation) {
        return getChamberOfCommerceCodeCandidatesByCode(organisation.getChamberOfCommerceCode(), organisation.getAtPath());
    }

    public OrganisationNameNumberViewModel getChamberOfCommerceCodeCandidatesByCode(final String code, final String atPath) {
        switch (atPath) {
            case "/FRA":
                return findCandidateForFranceByCode(code);
            //            return findCandidatesForFranceFake(code).get(0); // TODO: remove after developement

            default:
                return null;
        }
    }

    List<OrganisationNameNumberViewModel> findCandidatesForFranceFake(final String name) {
        List<OrganisationNameNumberViewModel> result = new ArrayList<>();
        result.add(new OrganisationNameNumberViewModel("ORG1", "123456789"));
        result.add(new OrganisationNameNumberViewModel("ORG2", "234234234"));
        result.add(new OrganisationNameNumberViewModel("ORG3", "345456567"));
        return result;
    }

    List<OrganisationNameNumberViewModel> findCandidatesForFranceByName(final String name) {
        List<OrganisationNameNumberViewModel> result = new ArrayList<>();
        SirenService sirenService = new SirenService();

        try {
            List<SirenResult> sirenResults = sirenService.getChamberOfCommerceCodes(name);
            for (SirenResult sirenResult : sirenResults) {
                String companyName = sirenResult.getCompanyName();
                String cocc = sirenResult.getChamberOfCommerceCode();
                result.add(new OrganisationNameNumberViewModel(companyName, cocc));
            }
        } catch (ClientProtocolException e) {
            messageService.warnUser(connectionWarning);
        }

        if (result.size() == 0) {
            messageService.warnUser(noResultsWarning);
        }

        return result;
    }

    OrganisationNameNumberViewModel findCandidateForFranceByCode(final String code) {
        SirenService sirenService = new SirenService();
        SirenResult sirenResult = null;
        try {
            sirenResult = sirenService.getCompanyName(code);
        } catch (ClientProtocolException e) {
            messageService.warnUser(connectionWarning);
        }
        if (sirenResult != null) {
            return new OrganisationNameNumberViewModel(sirenResult.getCompanyName(), code);
        } else {
            messageService.warnUser(noResultsWarning);
            return null;
        }
    }

    @Inject MessageService messageService;

}
