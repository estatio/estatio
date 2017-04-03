package org.estatio.app.services.order;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.order.OrderInvoiceImportLine"
)
@AllArgsConstructor
public class OrderInvoiceImportLine implements FixtureAwareRowHandler<OrderInvoiceImportLine> {

    public String title() {
        return "Order - Invoice Import Line";
    }

    public OrderInvoiceImportLine() {
    }

    @Getter @Setter
    private String orderNumber;
    @Getter @Setter
    private String charge;
    @Getter @Setter
    private LocalDate entryDate;
    @Getter @Setter
    private LocalDate orderDate;
    @Getter @Setter
    private String seller;
    @Getter @Setter
    private String orderDescription;
    @Getter @Setter
    private BigDecimal netAmount;
    @Getter @Setter
    private BigDecimal vatAmount;
    @Getter @Setter
    private BigDecimal grossAmount;
    @Getter @Setter
    private String orderApprovedBy;
    @Getter @Setter
    private String orderApprovedOn;
    @Getter @Setter
    private String projectReference;
    @Getter @Setter
    private String period;
    @Getter @Setter
    private String tax;
    @Getter @Setter
    private String invoiceNumber;
    @Getter @Setter
    private String invoiceDescription;
    @Getter @Setter
    private BigDecimal invoiceNetAmount;
    @Getter @Setter
    private BigDecimal invoiceVatAmount;
    @Getter @Setter
    private BigDecimal invoiceGrossAmount;
    @Getter @Setter
    private String invoiceTax;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    public OrderInvoiceImportLine handle(final OrderInvoiceImportLine previousRow){

        if (previousRow!=null) {
            if (charge == null && previousRow.getCharge() != null) {
                setCharge(previousRow.getCharge());
            }
            if (projectReference == null && previousRow.getProjectReference() != null){
                setProjectReference(previousRow.getProjectReference());
            }
            if (period == null && previousRow.getPeriod() != null){
                setPeriod(previousRow.getPeriod());
            }
            if (tax == null && previousRow.getTax() != null){
                setTax(previousRow.getTax());
            }
        }

        BigDecimal grossAmountToUse = null;
        if (grossAmount != null){
            grossAmountToUse = grossAmount;
        } else {
            if (netAmount != null & vatAmount != null){
                grossAmountToUse = netAmount.add(vatAmount);
            }
        }

        String invoiceTaxToUse = null;
        BigDecimal invoiceNetAmountToUse = null;
        BigDecimal invoiceGrossAmountToUse = null;
        BigDecimal invoiceVatAmountToUse = null;
        String invoiceDescriptionToUse = null;

        if (invoiceNumber != null) {
            invoiceNetAmountToUse = invoiceNetAmount != null ? invoiceNetAmount : netAmount;
            invoiceVatAmountToUse = invoiceVatAmount != null ? invoiceVatAmount : vatAmount;
            invoiceGrossAmountToUse = invoiceGrossAmount !=null ? invoiceGrossAmount : grossAmountToUse;
            invoiceTaxToUse = invoiceTax != null ? invoiceTax : tax;
            invoiceDescriptionToUse = invoiceDescription != null ? invoiceDescription : orderDescription;
        }

        OrderInvoiceImportLine lineItem = null;

        if (entryDate!=null || (invoiceNumber != null && invoiceNumber.matches(".*\\d.*"))) {
            lineItem = new OrderInvoiceImportLine(
                    orderNumber != null ? orderNumber.replace("Devis n°", "") : null,
                    charge,
                    entryDate,
                    orderDate,
                    seller,
                    orderDescription,
                    netAmount,
                    vatAmount,
                    grossAmountToUse,
                    orderApprovedBy,
                    orderApprovedOn,
                    projectReference,
                    period,
                    tax,
                    invoiceNumber != null ? invoiceNumber.replace("Facture n°", "") : null,
                    invoiceDescription,
                    invoiceNetAmountToUse,
                    invoiceVatAmountToUse,
                    invoiceGrossAmountToUse,
                    invoiceTaxToUse,
                    this.executionContext,
                    this.excelFixture2
            );
        }

        return lineItem;

    }

    @Override
    public void handleRow(final OrderInvoiceImportLine previousRow) {

            if(executionContext != null && excelFixture2 != null) {
                executionContext.addResult(excelFixture2,this.handle(previousRow));
            }

    }

}

