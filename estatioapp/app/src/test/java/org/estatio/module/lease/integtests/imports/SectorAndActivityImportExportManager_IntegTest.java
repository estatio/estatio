package org.estatio.module.lease.integtests.imports;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.xactn.TransactionService2;
import org.apache.isis.applib.value.Blob;
import org.estatio.module.lease.dom.occupancy.tags.ActivityRepository;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.SectorRepository;
import org.estatio.module.lease.fixtures.imports.SectorAndActivityImportFixture;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.imports.SectorAndActivityImportExportManager;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SectorAndActivityImportExportManager_IntegTest extends LeaseModuleIntegTestAbstract {

    List<FixtureResult> fixtureResults;

    @Before
    public void setup() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new SectorAndActivityImportFixture());
                executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb);
                executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb);
                fixtureResults = executionContext.getResults();
            }
        });
    }

    @Test
    public void importWorks() throws Exception {

        // given
        SectorAndActivityImportExportManager manager = new SectorAndActivityImportExportManager();
        Blob excelSheetOld = wrap(manager).download("test");
        transactionService2.nextTransaction();
        Blob excelSheetNew = (Blob) fixtureResults.get(0).getObject();
        Blob excelSheetWrong = (Blob) fixtureResults.get(1).getObject();
        assertThat(sectorRepository.allSectors()).hasSize(2);
        assertThat(activityRepository.allActivities()).hasSize(2);
        assertThat(sectorRepository.findByName("TEST1")).isNull();
        assertThat(sectorRepository.findByName("TEST2")).isNull();

        // when
        wrap(manager).upload(excelSheetNew);
        transactionService2.nextTransaction();

        // then
        assertThat(sectorRepository.allSectors()).hasSize(4);
        assertThat(activityRepository.allActivities()).hasSize(3);
        Sector newSector1 = sectorRepository.findByName("TEST1");
        assertThat(newSector1).isNotNull();
        assertThat(newSector1.getDescription()).isEqualTo("Test");
        assertThat(activityRepository.findBySectorAndName(newSector1, "TEST")).isNotNull();
        Sector newSector2 = sectorRepository.findByName("TEST2");
        assertThat(newSector2).isNotNull();
        assertThat(newSector2.getDescription()).isEqualTo("Test");

        // and when
        wrap(manager).upload(excelSheetOld);
        transactionService2.nextTransaction();

        // then
        assertThat(sectorRepository.allSectors()).hasSize(2);
        assertThat(activityRepository.allActivities()).hasSize(2);
        assertThat(sectorRepository.findByName("TEST1")).isNull();
        assertThat(sectorRepository.findByName("TEST2")).isNull();

        // expected
        expectedExceptions.expect(InvalidException.class);
        expectedExceptions.expectMessage("The following activities are already in use, cannot be removed: ALL, ELECTRIC\n" +
                        "The following sectors are already in use, cannot be removed: FASHION, ELECTRIC");

        // when
        wrap(manager).upload(excelSheetWrong);
        transactionService2.nextTransaction();
        assertThat(sectorRepository.allSectors()).hasSize(2);
        assertThat(activityRepository.allActivities()).hasSize(2);
        assertThat(sectorRepository.findByName("TEST1")).isNull();
        assertThat(sectorRepository.findByName("TEST2")).isNull();

    }

    @Inject
    SectorRepository sectorRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    TransactionService2 transactionService2;

}
