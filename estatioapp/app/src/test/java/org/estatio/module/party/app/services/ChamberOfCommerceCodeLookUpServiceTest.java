package org.estatio.module.party.app.services;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.party.dom.Organisation;

public class ChamberOfCommerceCodeLookUpServiceTest {

    @Test
    public void getChamberOfCommerceCodeCandidatesByOrganisation_works() throws Exception {

        // given
        ChamberOfCommerceCodeLookUpService service = new ChamberOfCommerceCodeLookUpService(){
            @Override
            List<OrganisationNameNumberViewModel> findCandidatesForFranceByName(final String name){
                return Arrays.asList(
                        new OrganisationNameNumberViewModel(),
                        new OrganisationNameNumberViewModel(),
                        new OrganisationNameNumberViewModel()
                );
            }
        };
        Organisation organisation = new Organisation(){
            @Override
            public String getAtPath(){
                return "/FRA";
            }
        };
        organisation.setName("Company");

        // when
        List<OrganisationNameNumberViewModel> result = service.getChamberOfCommerceCodeCandidatesByOrganisation(organisation);

        // then
        Assertions.assertThat(result.size()).isEqualTo(3);

    }

    @Test
    public void getChamberOfCommerceCodeCandidatesByCode_works() throws Exception {

        // given
        ChamberOfCommerceCodeLookUpService service = new ChamberOfCommerceCodeLookUpService(){
            @Override
            OrganisationNameNumberViewModel findCandidateForFranceByCode(final String code){
                return new OrganisationNameNumberViewModel();
            }
        };
        Organisation organisation = new Organisation(){
            @Override
            public String getAtPath(){
                return "/FRA";
            }
        };
        organisation.setName("Company");

        // when
        OrganisationNameNumberViewModel result = service.getChamberOfCommerceCodeCandidatesByCode(organisation.getName(), organisation.getAtPath());

        // then
        Assertions.assertThat(result).isNotNull();

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock MessageService mockMessageService;

    @Test
    public void find_Candidate_For_France_By_Code_works_when_no_result() throws Exception {

        // given
        ChamberOfCommerceCodeLookUpService service = new ChamberOfCommerceCodeLookUpService();
        service.messageService = mockMessageService;
        String noResultsWarning = "A connection to the external Siren service could be made, but no results were returned";

        // expect
        context.checking(new Expectations(){{
            allowing(mockMessageService).warnUser(noResultsWarning);
        }});

        // when
        service.findCandidateForFranceByCode("some cocc that returns no results");

    }

}