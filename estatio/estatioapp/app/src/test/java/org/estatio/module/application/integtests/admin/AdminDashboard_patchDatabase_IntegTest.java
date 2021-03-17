package org.estatio.module.application.integtests.admin;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.sessmgmt.SessionManagementService;
import org.apache.isis.core.metamodel.services.jdosupport.Persistable_datanucleusIdLong;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.fixtures.enums.Country_enum;

import org.estatio.module.application.app.AdminDashboard;
import org.estatio.module.application.app.AdministrationMenu;
import org.estatio.module.application.integtests.ApplicationModuleIntegTestAbstract;

public class AdminDashboard_patchDatabase_IntegTest extends ApplicationModuleIntegTestAbstract {

    @Before
    public void setup() {
    }

    @Test
    public void happyCase() throws Exception {

        // given
        final AdminDashboard dashboard = administrationMenu.openAdminDashboard();

        Country country = Country_enum.GBR.findUsing(serviceRegistry);

        final Long id = factoryService.mixin(Persistable_datanucleusIdLong.class, country).prop();

        // when
        final String newName = "Great Britain";
        final String sql = String.format(
                "UPDATE \"incodeCountry\".\"Country\" SET \"name\" = '%s' WHERE \"id\" = %d", newName, id);
        dashboard.patchDatabase(sql);

        sessionManagementService.nextSession();

        // then
        country = Country_enum.GBR.findUsing(serviceRegistry);

        Assertions.assertThat(country.getName()).isEqualTo(newName);
    }

    @Inject
    SessionManagementService sessionManagementService;
    @Inject
    FactoryService factoryService;
    @Inject
    AdministrationMenu administrationMenu;
}
