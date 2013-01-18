package org.estatio.viewer.wicket.app.administration;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Named;
import org.estatio.fixture.EstatioFixture;
import org.estatio.viewer.wicket.app.scheduler.EstatioSchedulerService;


@Named("Administration")
public class EstatioAdministrationService {

    public String installDemoFixtures() {
        EstatioFixture fixtures = container.newTransientInstance(EstatioFixture.class);
        fixtures.install();
        return "Demo fixtures successfully installed";
    }
    
    public void initializeSchedulerJobs() {
        scheduler.initializeJobs();
    }
    
    // {{ injected: Container
    private DomainObjectContainer container;
    
    public void setContainer(DomainObjectContainer container) {
        this.container = container;
    }
    // }}
    
    private EstatioSchedulerService scheduler;
    
    public void setSchedulerService(EstatioSchedulerService scheduler){
        this.scheduler = scheduler;
    }
}
