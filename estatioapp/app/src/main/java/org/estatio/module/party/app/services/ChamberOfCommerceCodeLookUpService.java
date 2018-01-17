package org.estatio.module.party.app.services;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

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

    final static String connectionWarning = "A connection to the external Siren service could not be made";
    final static String noResultsWarning = "A connection to the external Siren service could be made, but no results were returned";

    public List<OrganisationNameNumberViewModel> getChamberOfCommerceCodeCandidatesByOrganisation(final Organisation organisation) {
        return getChamberOfCommerceCodeCandidatesByOrganisation(organisation.getName(), organisation.getAtPath());
    }

    public List<OrganisationNameNumberViewModel> getChamberOfCommerceCodeCandidatesByOrganisation(final String name, final String atPath) {

        switch (atPath){
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
        switch (atPath){
        case "/FRA":
              return findCandidateForFranceByCode(code);
//            return findCandidatesForFranceFake(code).get(0); // TODO: remove after developement

        default:
            return null;
        }
    }


    List<OrganisationNameNumberViewModel> findCandidatesForFranceFake(final String name){
        List<OrganisationNameNumberViewModel> result = new ArrayList<>();
        result.add(new OrganisationNameNumberViewModel("ORG1", "123456789"));
        result.add(new OrganisationNameNumberViewModel("ORG2", "234234234"));
        result.add(new OrganisationNameNumberViewModel("ORG3", "345456567"));
        return result;
    }

    List<OrganisationNameNumberViewModel> findCandidatesForFranceByName(final String name){
        List<OrganisationNameNumberViewModel> result = new ArrayList<>();
        SirenService sirenService = new SirenService();
        List<SirenResult> sirenResults = null;
        try {
            sirenResults = sirenService.getChamberOfCommerceCodes(name);
        } catch (ConnectException e) {
            messageService.warnUser(connectionWarning);
        }
        for (SirenResult sirenResult : sirenResults){
            String companyName = sirenResult.getCompanyName();
            String cocc = sirenResult.getChamberOfCommerceCode();
            result.add(new OrganisationNameNumberViewModel(companyName, cocc));
        }
        if (result.size()==0) {
            messageService.warnUser(noResultsWarning);
        }
        return result;
    }

    OrganisationNameNumberViewModel findCandidateForFranceByCode(final String code){
        SirenService sirenService = new SirenService();
        String companyName = null;
        try {
            companyName = sirenService.getCompanyName(code).getCompanyName();
        } catch (ConnectException e) {
            messageService.warnUser(connectionWarning);
        }
        OrganisationNameNumberViewModel result = new OrganisationNameNumberViewModel(companyName, code);
        if (result==null) messageService.warnUser(noResultsWarning);
        return result;
    }

    @Inject MessageService messageService;

}
