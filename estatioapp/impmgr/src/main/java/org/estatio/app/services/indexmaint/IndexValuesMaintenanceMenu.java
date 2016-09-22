/*
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
package org.estatio.app.services.indexmaint;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Dflt;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepositoryForCountry;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named="Indices",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "60.1"
)
public class IndexValuesMaintenanceMenu extends UdoDomainService<IndexValuesMaintenanceMenu> {

    public IndexValuesMaintenanceMenu() {
        super(IndexValuesMaintenanceMenu.class);
    }


    // //////////////////////////////////////

    @PostConstruct
    public void init(final Map<String, String> properties) {
        super.init(properties);
        if(excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence="1")
    public Blob downloadIndexValues() {
        final String fileName = "IndexValues.xlsx";
        final List<IndexValueMaintLineItem> viewModels = Lists.newArrayList();
        return excelService.toExcel(viewModels, IndexValueMaintLineItem.class, fileName);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(sequence="2")
    public List<IndexValueMaintLineItem> uploadIndexValues(
            final @Named("Excel spreadsheet") Blob spreadsheet,
            final ApplicationTenancy applicationTenancy) {
        List<IndexValueMaintLineItem> lineItems = 
                excelService.fromExcel(spreadsheet, IndexValueMaintLineItem.class);
        for (IndexValueMaintLineItem lineItem : lineItems) {
            lineItem.setAtPath(applicationTenancy.getPath());
        }
        return lineItems;
    }

    public List<ApplicationTenancy> choices1UploadIndexValues() {
        return estatioApplicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default1UploadIndexValues() {
        return Dflt.of(choices1UploadIndexValues());
    }


    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

}
