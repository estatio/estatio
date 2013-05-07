package org.estatio.viewer.wicket.app.administration;

import org.estatio.appsettings.EstatioSetting;
import org.estatio.appsettings.EstatioSettingsService;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioFixture;
import org.estatio.fixture.index.IndexFixture;
import org.estatio.viewer.wicket.app.scheduler.EstatioSchedulerService;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

@Named("Administration")
public class EstatioAdministrationService {

    @MemberOrder(sequence = "1")
    public void initializeSchedulerJobs() {
        scheduler.initializeJobs();
    }

    @Prototype
    @MemberOrder(sequence = "2")
    public String installDemoFixtures() {
        EstatioFixture fixtures = container.newTransientInstance(EstatioFixture.class);
        fixtures.install();
        return "Demo fixtures successfully installed";
    }

    public String disableInstallDemoFixtures() {
        if (indexRepo.allIndices().size() == 0) {
            return null;
        }
        return "Demo fixtures already installed";
    }

    @Prototype
    @MemberOrder(sequence = "3")
    public String installIndexFixture() {
        IndexFixture fixture = container.newTransientInstance(IndexFixture.class);
        fixture.install();
        return "Index fixture successfully installed";
    }

    public String disableInstallIndexFixture() {
        if (indexRepo.allIndices().size() == 0) {
            return null;
        }
        return "Index fixture already installed";
    }

    public EstatioSetting applicatioSettings() {
        return settingsService.fetchSetting();
    }

    // {{ injected
    private DomainObjectContainer container;

    public void setContainer(DomainObjectContainer container) {
        this.container = container;
    }

    private EstatioSchedulerService scheduler;

    public void setSchedulerService(EstatioSchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    private Indices indexRepo;

    public void setIndexRepo(Indices indices) {
        this.indexRepo = indices;
    }

    private EstatioSettingsService settingsService;

    public void setSettingsService(EstatioSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // }}

}
