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
package org.estatio.dom.documents;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainService;
import org.estatio.domsettings.ApplicationSettingsServiceForEstatio;

@DomainService(nature = NatureOfService.DOMAIN)
public class ReportServerSettingsService extends UdoDomainService<ReportServerSettingsService> {

    public ReportServerSettingsService() {
        super(ReportServerSettingsService.class);
    }

    public final static String REPORT_SERVER_CONFIG_PROPERTY_KEY = "estatio.application.reportServerBaseUrl";
    public final static String REPORT_SERVER_CONFIG_PROPERTY_DEFAULT = "http://www.pdfpdf.com/samples/Sample5.PDF?name=";

    // //////////////////////////////////////


    @PostConstruct
    public void init(final Map<String,String> properties) {
        String reportServerUrl = properties.get(REPORT_SERVER_CONFIG_PROPERTY_KEY);
        if(reportServerUrl == null) {
            reportServerUrl = REPORT_SERVER_CONFIG_PROPERTY_DEFAULT;
        }
        this.cachedReportServerBaseUrl = reportServerUrl;
    }

    // //////////////////////////////////////


    private String cachedReportServerBaseUrl;

    @Programmatic
    public String fetchReportServerBaseUrl() {
        return cachedReportServerBaseUrl;
    }

    // //////////////////////////////////////

    @Inject
    ApplicationSettingsServiceForEstatio applicationSettingsService;


}
