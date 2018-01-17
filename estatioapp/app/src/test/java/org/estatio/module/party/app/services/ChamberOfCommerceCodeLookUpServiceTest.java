package org.estatio.module.party.app.services;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

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

}