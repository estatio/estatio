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
package org.estatio.domlink;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService.Root;

import org.estatio.domsettings.EstatioSettingsService;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class LinkContributions  {

    @Action(semantics = SemanticsOf.SAFE)
    public URL links(
            final Object domainObject,
            final Link link) throws MalformedURLException {
        final Root root = new Root(domainObject){
            @SuppressWarnings("unused")
            public String getReportServerBaseUrl() {
                return estatioSettingsService.fetchReportServerBaseUrl();
            }
        };
        final String urlStr = stringInterpolator.interpolate(root, link.getUrlTemplate());
        return new URL(urlStr);
    }
    
    public boolean hideLinks(final Object domainObject, final Link link) {
         return allForClassHierarchyOf(domainObject).isEmpty();
    }

    public List<Link> choices1Links(final Object domainObject, final Link link) {
        return allForClassHierarchyOf(domainObject);
    }

    private List<Link> allForClassHierarchyOf(final Object domainObject) {
        return linkRepository.findAllForClassHierarchy(domainObject);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private StringInterpolatorService stringInterpolator;

    @javax.inject.Inject
    private LinkRepository linkRepository;
    
    @javax.inject.Inject
    private EstatioSettingsService estatioSettingsService;
}
