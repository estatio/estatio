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
package org.estatio.app.index.maint;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import com.danhaywood.isis.domainservice.excel.applib.ExcelService;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.services.memento.MementoService.Memento;
import org.apache.isis.applib.value.Blob;
import org.estatio.dom.EstatioService;


@DomainService(menuOrder = "00")
@Named("Index Value Maintenance")
public class IndexValueMaintService extends EstatioService<IndexValueMaintService> {

    public IndexValueMaintService() {
        super(IndexValueMaintService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init() {
        if(excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////
    // download (action)
    // //////////////////////////////////////

    @MemberOrder(name="Indices", sequence="20.1")
    public Blob downloadIndexValues() {
        final String fileName = "IndexValues.xlsx";
        final List<IndexValueMaintLineItem> viewModels = Lists.newArrayList();
        return excelService.toExcel(viewModels, IndexValueMaintLineItem.class, fileName);
    }

    // //////////////////////////////////////
    // upload (action)
    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="Indices", sequence="20.2")
    public List<IndexValueMaintLineItem> uploadIndexValues(
            final @Named("Excel spreadsheet") Blob spreadsheet) {
        List<IndexValueMaintLineItem> lineItems = 
                excelService.fromExcel(spreadsheet, IndexValueMaintLineItem.class);
        return lineItems;
    }

    
    // //////////////////////////////////////
    // memento for lease term "line item"
    // //////////////////////////////////////
    
    String mementoFor(final IndexValueMaintLineItem lineItem) {
        final Memento memento = getMementoService().create();

        memento.set("reference", lineItem.getReference());
        
        memento.set("baseFactor", lineItem.getBaseFactor());
        memento.set("baseStartDate", lineItem.getBaseStartDate());
        
        memento.set("valueStartDate", lineItem.getValueStartDate());
        memento.set("value", lineItem.getValue());

        return memento.asString();
    }
    
    void initOf(final String mementoStr, final IndexValueMaintLineItem lineItem) {
        final Memento memento = getMementoService().parse(mementoStr);

        lineItem.setReference(memento.get("reference", String.class));

        lineItem.setBaseFactor(memento.get("baseFactor", BigDecimal.class));
        lineItem.setBaseStartDate(memento.get("baseStartDate", LocalDate.class));
        
        lineItem.setValueStartDate(memento.get("valueStartDate", LocalDate.class));
        lineItem.setValue(memento.get("value", BigDecimal.class));
    }

    IndexValueMaintLineItem newLineItem(IndexValueMaintLineItem lineItem) {
        final String memento = mementoFor(lineItem);
        return getContainer().newViewModelInstance(IndexValueMaintLineItem.class, memento);
    }

    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;
}
