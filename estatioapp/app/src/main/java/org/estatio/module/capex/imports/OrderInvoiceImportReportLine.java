package org.estatio.module.capex.imports;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "orders.OrderInvoiceImportReportLine"
)
@Getter @Setter
@AllArgsConstructor
public class OrderInvoiceImportReportLine {

    private String projectReference;

    private String period;

    private Integer numberOfOrderItems;

    private BigDecimal orderNetTotal;

    private BigDecimal orderVatTotal;

    private BigDecimal orderGrossTotal;

    private Integer numberOfInvoiceItems;

    private BigDecimal invoiceNetTotal;

    private BigDecimal invoiceVatTotal;

    private BigDecimal invoiceGrossTotal;

}
