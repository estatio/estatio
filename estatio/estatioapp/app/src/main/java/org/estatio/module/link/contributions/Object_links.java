/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
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
package org.estatio.module.link.contributions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService.Root;

import org.estatio.module.link.dom.Link;
import org.estatio.module.link.dom.LinkRepository;
import org.estatio.module.settings.dom.ReportServerSettingsService;

// TODO: perhaps logically this could live in org.estatio.module.document (only other client of ReportServerSettingsService)
@Mixin
public class Object_links {

    //region > constructor
    private final Object domainObject;

    public Object_links(final Object domainObject) {
        this.domainObject = domainObject;
    }
    //endregion


    @Action(semantics = SemanticsOf.SAFE)
    public URL $$(final Link link) throws MalformedURLException {
        final Root root = new Root(domainObject){
            @SuppressWarnings("unused")
            public String getReportServerBaseUrl() {
                return reportServerSettingsService.fetchReportServerBaseUrl();
            }
        };
        final String urlStr = stringInterpolator.interpolate(root, link.getUrlTemplate());
        return new URL(urlStr);
    }
    
    public boolean hide$$() {
         return choices0$$().isEmpty();
    }

    public List<Link> choices0$$() {
        return linkRepository.findAllForClassHierarchy(domainObject);
    }


    //region > injected services

    @javax.inject.Inject
    private StringInterpolatorService stringInterpolator;

    @javax.inject.Inject
    private LinkRepository linkRepository;
    
    @javax.inject.Inject
    private ReportServerSettingsService reportServerSettingsService;

    //endregion

}
