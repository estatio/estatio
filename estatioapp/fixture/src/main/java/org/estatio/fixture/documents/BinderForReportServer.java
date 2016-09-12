
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
package org.estatio.fixture.documents;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.documents.dom.applicability.Binder;
import org.incode.module.documents.dom.docs.DocumentTemplate;

import org.estatio.dom.appsettings.EstatioSettingsService;

/**
 * Uses {@link StringInterpolatorService} to create data model, and requires domain object to implement {@link WithApplicationTenancy}.
 */
public class BinderForReportServer implements Binder {

    public Binder.Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject) {

        final String baseUrl = estatioSettingsService.fetchReportServerBaseUrl();

        final StringInterpolatorService.Root dataModel = new StringInterpolatorService.Root(domainObject) {
            @SuppressWarnings("unused")
            public String getReportServerBaseUrl() {
                return baseUrl;
            }
        };
        return new Binding(dataModel, domainObject);
    }

    @javax.inject.Inject
    private EstatioSettingsService estatioSettingsService;

}
