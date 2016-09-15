
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

import com.google.common.collect.Lists;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.appsettings.EstatioSettingsService;
import org.estatio.dom.documents.datamodels.RootForReportServer;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

/**
 * Creates a dataModel to be used with {@link StringInterpolatorService} for both content and subject;
 * requires domain object to implement {@link WithApplicationTenancy}.
 *
 * The input object must be a {@link InvoiceSummaryForPropertyDueDateStatus}, used to determine what to attach the
 * resultant document.
 */
public class BinderForReportServerForInvoiceSummaryForPropertyDueDateStatus implements Binder {

    public Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject) {

        if(!(domainObject instanceof InvoiceSummaryForPropertyDueDateStatus)) {
            throw new IllegalArgumentException("Domain object must be of type: InvoiceSummaryForPropertyDueDateStatus");
        }

        // dataModel
        final String baseUrl = estatioSettingsService.fetchReportServerBaseUrl();
        final StringInterpolatorService.Root dataModel = new RootForReportServer(domainObject, baseUrl);

        // attachTo
        final InvoiceSummaryForPropertyDueDateStatus viewModel = (InvoiceSummaryForPropertyDueDateStatus) domainObject;
        final List<Object> attachTo = Lists.newArrayList();
        attachTo.add(viewModel.getSeller());
        attachTo.addAll(viewModel.getInvoices());

        // binding
        return new Binding(dataModel, dataModel, attachTo);
    }

    @javax.inject.Inject
    EstatioSettingsService estatioSettingsService;


}
