package org.estatio.module.party.app.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.joda.time.LocalDate;

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

    private final static String CONNECTION_WARNING = "A connection to the external Siren service could not be made";
    private final static String NO_RESULTS_WARNING = "A connection to the external Siren service could be made, but no results were returned";

    public static final List<String> LEGAL_FORMS = Arrays.asList("SA", "SAS", "SASU", "SARL", "EURL", "SCI", "SNC");

    public List<OrganisationNameNumberViewModel> getChamberOfCommerceCodeCandidatesByOrganisation(final Organisation organisation) {
        return getChamberOfCommerceCodeCandidatesByOrganisation(organisation.getName(), organisation.getAtPath());
    }

    public List<OrganisationNameNumberViewModel> getChamberOfCommerceCodeCandidatesByOrganisation(final String name, final String atPath) {
        if (atPath == null)
            return Collections.emptyList();

        return atPath.startsWith("/FRA") ?
                findCandidatesForFranceByName(name) :
                Collections.emptyList();
    }

    public OrganisationNameNumberViewModel getChamberOfCommerceCodeCandidatesByCode(final Organisation organisation) {
        return getChamberOfCommerceCodeCandidatesByCode(organisation.getChamberOfCommerceCode(), organisation.getAtPath());
    }

    public OrganisationNameNumberViewModel getChamberOfCommerceCodeCandidatesByCode(final String code, final String atPath) {
        return atPath.startsWith("/FRA") ?
                findCandidateForFranceByCode(code) :
                null;
    }

    List<OrganisationNameNumberViewModel> findCandidatesForFranceByName(final String name) {
        List<OrganisationNameNumberViewModel> result = new ArrayList<>();

        try {
            List<SirenResult> sirenResults = sirenService.getChamberOfCommerceCodes(name);
            for (SirenResult sirenResult : sirenResults) {
                String companyName = sirenResult.getCompanyName();
                String cocc = sirenResult.getChamberOfCommerceCode();
                LocalDate entryDate = sirenResult.getEntryDate();
                result.add(new OrganisationNameNumberViewModel(companyName, cocc, entryDate));
            }
        } catch (ClientProtocolException e) {
            messageService.warnUser(CONNECTION_WARNING);
        }

        if (result.size() == 0) {
            messageService.warnUser(NO_RESULTS_WARNING);
        }

        return result;
    }

    OrganisationNameNumberViewModel findCandidateForFranceByCode(final String code) {
        try {
            SirenResult sirenResult = sirenService.getCompanyName(code);

            if (sirenResult != null) {
                return new OrganisationNameNumberViewModel(sirenResult.getCompanyName(), code, sirenResult.getEntryDate());
            } else {
                messageService.warnUser(NO_RESULTS_WARNING);
            }
        } catch (ClientProtocolException e) {
            messageService.warnUser(CONNECTION_WARNING);
        }

        return null;
    }

    String filterLegalFormsFromOrganisationName(final String name ) {
        return Arrays.stream(name.split(" "))
                .filter(element -> !LEGAL_FORMS.contains(element))
                .filter(element -> !LEGAL_FORMS.contains(element.replace(".", "")))
                .collect(Collectors.joining(" "));
    }

    @Inject MessageService messageService;

    @Inject SirenService sirenService;

}
