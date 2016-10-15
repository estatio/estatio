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
package org.estatio.dom.documents.binders;

import java.util.List;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.document.dom.impl.applicability.Binder;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.appsettings.EstatioSettingsService;

/**
 * Creates a dataModel to be used with {@link StringInterpolatorService} for both content and subject;
 * requires domain object to implement {@link WithApplicationTenancy}.
 */
public abstract class BinderForReportServerAbstract implements Binder {

    public Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject, final String additionalTextIfAny) {

        final String baseUrl = estatioSettingsService.fetchReportServerBaseUrl();

        // dataModel
        final DataModel dataModel = new DataModel(domainObject, baseUrl);

        // binding
        return new Binding(dataModel, determineAttachTo(domainObject));
    }

    protected abstract List<Object> determineAttachTo(final Object domainObject);

    @javax.inject.Inject
    EstatioSettingsService estatioSettingsService;

    /**
     * Intended to be used as a dataModel to pass into render strategies that use the
     * {@link StringInterpolatorService} with the <code>${reportServerBaseUrl}</code> property to be interpolated.
     */
    public static class DataModel extends StringInterpolatorService.Root {

        private final String reportServerBaseUrl;

        public DataModel(final Object domainObject, final String reportServerBaseUrl) {
            super(domainObject);
            this.reportServerBaseUrl = reportServerBaseUrl;
        }

        public String getReportServerBaseUrl() {
            return reportServerBaseUrl;
        }
    }
}
