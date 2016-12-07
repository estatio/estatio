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
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Dflt;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.country.CountryServiceForCurrentUser;
import org.estatio.dom.country.EstatioApplicationTenancyRepositoryForCountry;

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

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence="1")
    public Blob downloadIndexValues() {
        final String fileName = "IndexValues.xlsx";
        final List<IndexValueMaintLineItem> viewModels = Lists.newArrayList();
        return excelService.toExcel(viewModels, IndexValueMaintLineItem.class, IndexValueMaintLineItem.class.getSimpleName(), fileName);
    }

    // //////////////////////////////////////

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence="2")
    public List<IndexValueMaintLineItem> uploadIndexValues(
            // @Parameter(fileAccept = ".xlsx")        // commented out until confirmed that ".xls" is not also in use (EST-948)
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet,
            final Country country) {

        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);

        List<IndexValueMaintLineItem> lineItems = 
                excelService.fromExcel(spreadsheet, IndexValueMaintLineItem.class, IndexValueMaintLineItem.class.getSimpleName());
        for (IndexValueMaintLineItem lineItem : lineItems) {
            lineItem.setAtPath(applicationTenancy.getPath());
        }
        return lineItems;
    }

    public List<Country> choices1UploadIndexValues() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public Country default1UploadIndexValues() {
        return Dflt.of(choices1UploadIndexValues());
    }


    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;

}
