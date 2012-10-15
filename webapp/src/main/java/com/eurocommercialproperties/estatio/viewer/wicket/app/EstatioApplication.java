package com.eurocommercialproperties.estatio.viewer.wicket.app;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

import org.apache.isis.viewer.wicket.ui.app.registry.ComponentFactoryRegistrar;
import org.apache.isis.viewer.wicket.viewer.IsisWicketApplication;


public class EstatioApplication extends IsisWicketApplication {

    private static final long serialVersionUID = 1L;

    @Override
    protected Module newIsisWicketModule() {
        final Module isisDefaults = super.newIsisWicketModule();
        final Module estatioOverrides = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ComponentFactoryRegistrar.class).to(ComponentFactoryRegistrarForEstatio.class);
                
                bind(String.class).annotatedWith(Names.named("applicationName")).toInstance("Estatio");
                bind(String.class).annotatedWith(Names.named("applicationCss")).toInstance("css/application.css");
                bind(String.class).annotatedWith(Names.named("applicationJs")).toInstance("scripts/application.js");
                bind(String.class).annotatedWith(Names.named("welcomeMessage")).toInstance("This is Estatio - an open source property management system implemented using Apache Isis.");
                bind(String.class).annotatedWith(Names.named("aboutMessage")).toInstance("Estatio v0.1.0");
            }
        };

        return Modules.override(isisDefaults).with(estatioOverrides);
    }
}
