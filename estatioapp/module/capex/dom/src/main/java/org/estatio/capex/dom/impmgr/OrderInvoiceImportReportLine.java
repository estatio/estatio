package org.estatio.capex.dom.impmgr;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.impmgr.OrderInvoiceImportReportLine"
)
@Getter @Setter
@AllArgsConstructor
public class OrderInvoiceImportReportLine {

    private String projectReference;

    private String period;

    private Integer numberOfOrders;

    private BigDecimal orderNetTotal;

    private BigDecimal orderVatTotal;

    private BigDecimal orderGrossTotal;

    private Integer numberOfInvoices;

    private BigDecimal invoiceNetTotal;

    private BigDecimal invoiceVatTotal;

    private BigDecimal invoiceGrossTotal;

}
