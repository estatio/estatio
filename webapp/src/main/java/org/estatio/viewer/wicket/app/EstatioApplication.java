/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.viewer.wicket.app;

import java.io.InputStream;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.google.inject.util.Providers;

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
                bind(String.class).annotatedWith(Names.named("aboutMessage")).toInstance("Estatio");
                bind(InputStream.class).annotatedWith(Names.named("metaInfManifest")).toProvider(Providers.of(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF")));
            }
        };
        return Modules.override(isisDefaults).with(estatioOverrides);
    }

}

