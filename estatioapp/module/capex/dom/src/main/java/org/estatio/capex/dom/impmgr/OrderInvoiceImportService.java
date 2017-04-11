/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.capex.dom.impmgr;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

@DomainService(nature = NatureOfService.DOMAIN)
public class OrderInvoiceImportService {

    @PostConstruct
    public void init() {
        if (excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    public List<OrderInvoiceLine> createLines(final String sheetNameMatcher, final Blob spreadsheet){
        List<OrderInvoiceLine> orderInvoiceLines = new ArrayList<>();
        List<List<?>> res = excelService.fromExcel(
                spreadsheet,
                sheetName -> {
                    if(sheetName.startsWith(sheetNameMatcher)) {
                        return new WorksheetSpec(
                                OrderInvoiceImportHandler.class,
                                sheetName,
                                Mode.RELAXED);
                    }
                    else
                        return null;
                }
        );
        List<List<OrderInvoiceImportHandler>> worksheetHandlers = (List)res;

        for (final List<OrderInvoiceImportHandler> rowHandlers : worksheetHandlers){
            OrderInvoiceImportHandler previous = null;
            for (OrderInvoiceImportHandler handler : rowHandlers){
                final OrderInvoiceLine invoiceLine = handler.handle(previous);
                if (invoiceLine !=null) {
                    orderInvoiceLines.add(invoiceLine);
                }
                previous = handler;
            }
        }
        return orderInvoiceLines;
    }

    @Programmatic
    public Blob createSheet(final List<OrderInvoiceLine> lines){
        return excelService.toExcel(lines, OrderInvoiceLine.class, "OrderInvoiceLine", "result.xlsx");
    }

    @Inject
    private ExcelService excelService;


}
