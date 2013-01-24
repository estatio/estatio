package org.estatio.viewer.wicket.app;

import java.util.List;

import javax.servlet.ServletContext;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

import org.apache.isis.core.commons.config.IsisConfigurationBuilder;
import org.apache.isis.core.commons.config.IsisConfigurationBuilderPrimer;
import org.apache.isis.core.commons.config.IsisConfigurationBuilderResourceStreams;
import org.apache.isis.core.commons.resource.ResourceStreamSourceComposite;
import org.apache.isis.core.commons.resource.ResourceStreamSourceContextLoaderClassPath;
import org.apache.isis.core.commons.resource.ResourceStreamSourceCurrentClassClassPath;
import org.apache.isis.core.commons.resource.ResourceStreamSourceFileSystem;
import org.apache.isis.core.webapp.WebAppConstants;
import org.apache.isis.core.webapp.config.ResourceStreamSourceForWebInf;
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
                bind(String.class).annotatedWith(Names.named("welcomeMessage")).toInstance("This is Estatio - a property management system implemented using Apache Isis.");
                bind(String.class).annotatedWith(Names.named("aboutMessage")).toInstance("Estatio v0.1.0");
            }
        };
        return Modules.override(isisDefaults).with(estatioOverrides);
    }

    
    // TODO: this has been temporarily copied down from IsisWicketApplication, remove once the Isis code has been committed and promoted etc etc
    protected IsisConfigurationBuilder createConfigBuilder(final ServletContext servletContext) {
        
        final String configLocation = servletContext.getInitParameter(WebAppConstants.CONFIG_DIR_PARAM);
        final ResourceStreamSourceComposite compositeSource = new ResourceStreamSourceComposite(
                new ResourceStreamSourceForWebInf(servletContext),
                ResourceStreamSourceContextLoaderClassPath.create(),
                new ResourceStreamSourceCurrentClassClassPath());

        if ( configLocation != null ) {
            //LOG.info( "Config override location: " + configLocation );
            compositeSource.addResourceStreamSource(ResourceStreamSourceFileSystem.create(configLocation));
        } else {
            //LOG.info( "Config override location: No override location configured!" );
        }
        
        final IsisConfigurationBuilder configurationBuilder = new IsisConfigurationBuilderResourceStreams(compositeSource);
        
        primeConfigurationBuilder(configurationBuilder, servletContext);
        configurationBuilder.addDefaultConfigurationResources();
        return configurationBuilder;
    }


    // TODO: this has been temporarily copied down from IsisWicketApplication, remove once the Isis code has been committed and promoted etc etc
    @SuppressWarnings("unchecked")
    private static void primeConfigurationBuilder(final IsisConfigurationBuilder isisConfigurationBuilder, final ServletContext servletContext) {
        final List<IsisConfigurationBuilderPrimer> isisConfigurationBuilderPrimers = (List<IsisConfigurationBuilderPrimer>) servletContext.getAttribute(WebAppConstants.CONFIGURATION_PRIMERS_KEY);
        if (isisConfigurationBuilderPrimers == null) {
            return;
        }
        for (final IsisConfigurationBuilderPrimer isisConfigurationBuilderPrimer : isisConfigurationBuilderPrimers) {
            isisConfigurationBuilderPrimer.primeConfigurationBuilder(isisConfigurationBuilder);
        }
    }


}

