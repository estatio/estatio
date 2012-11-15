package com.eurocommercialproperties.estatio.viewer.wicket.app.fixtures;

import com.eurocommercialproperties.estatio.fixture.EstatioFixture;

import org.apache.isis.applib.DomainObjectContainer;

public class DemoFixturesService {

    public void installFixtures() {
        EstatioFixture fixtures = container.newTransientInstance(EstatioFixture.class);
        fixtures.install();
        container.informUser("Demo fixtures installed successfully");
    }
    
    // {{ injected: Container
    private DomainObjectContainer container;
    
    public void setContainer(DomainObjectContainer container) {
        this.container = container;
    }
    // }}
}
