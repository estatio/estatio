package org.estatio.module.party.app.services;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

public class OrganisationNameNumberViewModel_Test {

    @Test
    public void title_works() throws Exception {

        // given
        final String organisationName = "Some organisation name";
        final String chamberOfCommerceCode = "123456789";
        final LocalDate entryDate = LocalDate.parse("2019-01-01");
        OrganisationNameNumberViewModel vm = new OrganisationNameNumberViewModel(organisationName, chamberOfCommerceCode, entryDate);

        // then
        Assertions.assertThat(vm.title()).isEqualTo("Some organisation name 123456789 [2019-01-01]");

    }

}