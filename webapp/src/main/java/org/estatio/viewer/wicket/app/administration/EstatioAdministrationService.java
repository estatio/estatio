package org.estatio.viewer.wicket.app.administration;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.index.Indices;
import org.estatio.fixture.EstatioFixture;
import org.estatio.fixture.index.IndexFixture;
import org.estatio.fixturescripts.FixtureScript;
import org.estatio.services.appsettings.EstatioSetting;
import org.estatio.services.appsettings.EstatioSettingsService;
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
        return !propertiesService.allProperties().isEmpty() ? "Demo fixtures already installed" : null;
    }

    @Prototype
    @MemberOrder(sequence = "3")
    public String installIndexFixture() {
        IndexFixture fixture = container.newTransientInstance(IndexFixture.class);
        fixture.install();
        return "Index fixture successfully installed";
    }

    public String disableInstallIndexFixture() {
        return !indexRepo.allIndices().isEmpty() ? "Index fixture already installed" : null;
    }

    @MemberOrder(sequence = "9")
    @Prototype
    public void runScript(FixtureScript fixtureScript) {
        fixtureScript.run(container);
    }

    public FixtureScript default0RunScript() {
        return FixtureScript.GenerateTopModelInvoice;
    }

    public EstatioSetting applicationSettings() {
        return settingsService.fetchSetting();
    }

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

    private Properties propertiesService;

    public void setPropertiesService(Properties propertiesService) {
        this.propertiesService = propertiesService;
    }

    private EstatioSettingsService settingsService;

    public void setSettingsService(EstatioSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // }}

}
