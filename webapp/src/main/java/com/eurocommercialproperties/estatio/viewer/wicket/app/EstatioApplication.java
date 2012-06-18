package com.eurocommercialproperties.estatio.viewer.wicket.app;

import org.apache.isis.viewer.wicket.viewer.IsisWicketApplication;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;


public class EstatioApplication extends IsisWicketApplication {

    private static final long serialVersionUID = 1L;

    @Override
    protected Module newIsisWicketModule() {
        return Modules.override(
                super.newIsisWicketModule()).with(
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            //bindConstant().annotatedWith(ApplicationCssUrl.class).to("myapp.css");
                        }
                    });
    }
}
