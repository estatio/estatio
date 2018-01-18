package org.estatio.module.party.app.services;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OrganisationNameNumberViewModel_Test {

    @Test
    public void title_works() throws Exception {

        // given
        OrganisationNameNumberViewModel vm = new OrganisationNameNumberViewModel();
        final String organisationName = "Some organisation name";
        final String chamberOfCommerceCode = "123456789";

        // when
        vm.setOrganisationName(organisationName);
        vm.setChamberOfCommerceCode(chamberOfCommerceCode);

        // then
        Assertions.assertThat(vm.title()).isEqualTo("Some organisation name 123456789");

    }

}