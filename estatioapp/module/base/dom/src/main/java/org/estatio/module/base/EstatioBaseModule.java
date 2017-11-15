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
package org.estatio.module.base;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.ExcelModule;
import org.isisaddons.module.pdfbox.dom.PdfBoxModule;
import org.isisaddons.module.poly.PolyModule;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.servletapi.ServletApiModule;
import org.isisaddons.module.settings.SettingsModule;
import org.isisaddons.module.stringinterpolator.StringInterpolatorModule;
import org.isisaddons.module.togglz.TogglzModule;
import org.isisaddons.wicket.excel.cpt.ui.ExcelUiModule;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.FullCalendar2ApplibModule;
import org.isisaddons.wicket.fullcalendar2.cpt.ui.FullCalendar2UiModule;
import org.isisaddons.wicket.gmap3.cpt.applib.Gmap3ApplibModule;
import org.isisaddons.wicket.gmap3.cpt.service.Gmap3ServiceModule;
import org.isisaddons.wicket.gmap3.cpt.ui.Gmap3UiModule;
import org.isisaddons.wicket.pdfjs.cpt.PdfjsCptModule;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.communications.dom.CommunicationsModule;
import org.incode.module.country.dom.CountryModule;
import org.incode.module.docfragment.dom.DocFragmentModuleDomModule;
import org.incode.module.docrendering.freemarker.dom.FreemarkerDocRenderingModule;
import org.incode.module.docrendering.stringinterpolator.dom.StringInterpolatorDocRenderingModule;
import org.incode.module.docrendering.xdocreport.dom.XDocReportDocRenderingModule;
import org.incode.module.document.dom.DocumentModule;
import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.base.platform.applib.ModuleAbstract;

public final class EstatioBaseModule extends ModuleAbstract {

    /**
     * For now, we'll reference all of the incode platform that we need.
     * Later on, we might introduce proxies (see EstatioCountryModule as proxy
     * for CountryModule).
     * @return
     */
    @Override
    public Set<Class<?>> getDependenciesAsClass() {
        return Sets.newHashSet(

                // lib
                // (nothing for incode-module-base-dom)
                // (nothing for incode-module-fixturesupport-dom)
                ExcelModule.class,
                FreemarkerDocRenderingModule.class,
                StringInterpolatorDocRenderingModule.class,
                XDocReportDocRenderingModule.class,
                PdfBoxModule.class,
                PolyModule.class,
                ServletApiModule.class,
                SettingsModule.class,
                StringInterpolatorModule.class,


                // generic dom
                ClassificationModule.class,
                CommunicationsModule.class,
                CountryModule.class,
                DocFragmentModuleDomModule.class,
                DocumentModule.class,


                // spi
                SecurityModule.class,

                // wkt
                ExcelUiModule.class,
                FullCalendar2ApplibModule.class,
                FullCalendar2UiModule.class,
                Gmap3ApplibModule.class,
                Gmap3UiModule.class,
                Gmap3ServiceModule.class,
                PdfjsCptModule.class,

                // ext
                TogglzModule.class

                );
    }


    @Override
    public FixtureScript getRefDataSetupFixture() {
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new ApplicationTenancy_enum.PersistScript());
            }
        };
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                deleteFrom(ApplicationTenancy.class);
            }
        };
    }



    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }


}
