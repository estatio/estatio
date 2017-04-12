package org.estatio.capex.dom.impmgr;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelMetaDataEnabled;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import lombok.Getter;
import lombok.Setter;

public class OrderInvoiceImportHandler implements FixtureAwareRowHandler<OrderInvoiceImportHandler>, ExcelMetaDataEnabled {

    @Getter @Setter
    private String excelSheetName;
    @Getter @Setter
    private Integer excelRowNumber;
    @Getter @Setter
    private String status;
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
    private LocalDate orderApprovedOn;
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

    public OrderInvoiceLine handle(final OrderInvoiceImportHandler previousRow){

        if (getOrderDate()==null) {
            setOrderDate(getEntryDate());
        }

        if (previousRow != null) {

            // support sparse population for orderdate (or derived)
            if (getOrderDate() == null && previousRow.getOrderDate() != null){
                setOrderDate(previousRow.getOrderDate());
            }
            // support sparse population for charge
            if (getCharge() == null && previousRow.getCharge() != null) {
                setCharge(previousRow.getCharge());
            }
            // support sparse population for project reference
            if (getProjectReference() == null && previousRow.getProjectReference() != null){
                setProjectReference(previousRow.getProjectReference());
            }
            // support sparse population for period
            if (getPeriod() == null && previousRow.getPeriod() != null){
                setPeriod(previousRow.getPeriod());
            }
            // support sparse population for tax
            if (getTax() == null && previousRow.getTax() != null){
                setTax(previousRow.getTax());
            }

            // copy seller and order description when multiple invoice lines
            if (getEntryDate()==null && invoiceNumberToUse()!=null){
                if (getSeller()==null && previousRow.getSeller()!=null){
                    setSeller(previousRow.getSeller());
                }
                if (getOrderDescription()==null && previousRow.getOrderDescription()!=null){
                    setOrderDescription(previousRow.getOrderDescription());
                }
            }
        }

        OrderInvoiceLine lineItem = null;

        if (getEntryDate()!=null || invoiceNumberToUse()!=null) {
            lineItem = new OrderInvoiceLine(
                    getExcelSheetName(),
                    getExcelRowNumber(),
                    validateRow(),
                    getCharge(),
                    clean(getOrderNumber()),
                    getEntryDate(),
                    getOrderDate(),
                    getSeller(),
                    getOrderDescription(),
                    netAmountToUse(),
                    vatAmountToUse(),
                    grossAmountToUse(),
                    clean(getOrderApprovedBy()),
                    getOrderApprovedOn(),
                    getProjectReference(),
                    getPeriod(),
                    taxToUse(),
                    invoiceNumberToUse(),
                    invoiceDescriptionToUse(),
                    invoiceNetAmountToUse(),
                    invoiceVatAmountToUse(),
                    invoiceGrossAmountToUse(),
                    invoiceTaxToUse()
            );
        }

        return lineItem;
    }

    private String clean(final String input){
        if (input==null){
            return null;
        }
        String result = input.trim();
        result = result.replace("Devis n°","");
        result = result.replace("Devis ","");
        result = result.replace("Devis","");
        result = result.replace("Accord ","");
        result = result.replace("Facture n°","");
        result = result.replace("Facture ","");
        result = result.replace("Facture","");
        return result.trim();
    }

    private String convert(final String input){

        if (input==null){
            return null;
        }

        // tax
        String result = input;
        if (input.toLowerCase().equals("tva normale")){
            result = "FRF";
        }
        if (input.toLowerCase().equals("exempt")){
            result = "FRE";
        }
        return result;
    }

    private BigDecimal netAmountToUse(){
        return getNetAmount()!=null ? getNetAmount().setScale(2, BigDecimal.ROUND_HALF_UP):null;
    }

    private BigDecimal vatAmountToUse(){
        return getVatAmount()!=null ? getVatAmount().setScale(2, BigDecimal.ROUND_HALF_UP):null;
    }

    private BigDecimal grossAmountToUse(){
        if (getGrossAmount() != null){
            return getGrossAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        if (getNetAmount()!= null && getVatAmount() != null) {
            return getNetAmount().add(getVatAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        if (getNetAmount()!= null && Objects.equals(taxToUse(), "FRE")){
            return getNetAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    private String taxToUse(){
        return getTax() != null ? convert(getTax()) : null;
    }

    private String invoiceNumberToUse(){
        if (getInvoiceNumber()==null || !getInvoiceNumber().matches(".*\\d.*")){
            return null;
        }
        return clean(getInvoiceNumber());
    }

    private String invoiceTaxToUse(){
        if (getInvoiceNumber()==null){
            return null;
        }
        return getInvoiceTax() != null ? convert(getInvoiceTax()) : taxToUse();
    }

    private BigDecimal invoiceNetAmountToUse(){
        if (getInvoiceNumber()==null){
            return null;
        }
        return getInvoiceNetAmount() != null ? getInvoiceNetAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : netAmountToUse();
    }

    private BigDecimal invoiceGrossAmountToUse(){
        if (getInvoiceNumber()==null){
            return null;
        }
        return getInvoiceGrossAmount() !=null ? getInvoiceGrossAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : grossAmountToUse();
    }

    private BigDecimal invoiceVatAmountToUse(){
        if (getInvoiceNumber()==null){
            return null;
        }
        return getInvoiceVatAmount() != null ? getInvoiceVatAmount().setScale(2, BigDecimal.ROUND_HALF_UP) : vatAmountToUse();
    }

    private String invoiceDescriptionToUse(){
        if (getInvoiceNumber()==null){
            return null;
        }
        return getInvoiceDescription() != null ? getInvoiceDescription() : getOrderDescription();
    }

    private String validateRow(){
        StringBuilder b = new StringBuilder();

        // both order and invoice validation
        if (getCharge()==null || getCharge().equals("")){
            b.append("no charge; ");
        }
        if (getProjectReference()==null || getProjectReference().equals("")){
            b.append("no project reference; ");
        }
        if (getSeller() == null || getSeller().equals("")) {
            b.append("no seller; ");
        }
        if (getPeriod() == null || getPeriod().equals("")) {
            b.append("no period; ");
        }
        // order validation
        if (getEntryDate()!=null) {
            if (getVatAmount() == null && taxToUse()!=null && !taxToUse().equals("FRE")) {
                b.append("no vat amount; ");
            }
            if (getNetAmount() == null) {
                b.append("no net amount; ");
            }
            if (grossAmountToUse() == null){
                b.append("no gross amount; ");
            }
            if (getTax() == null || getTax().equals("")) {
                b.append("no tax; ");
            }
        }

        // invoice validation
        if (getInvoiceNumber()!=null){
            if (invoiceNetAmountToUse()==null){
                b.append("no invoice net amount; ");
            }
            if (invoiceVatAmountToUse()==null && invoiceTaxToUse()!=null && !invoiceTaxToUse().equals("FRE")){
                b.append("no invoice vat amount; ");
            }
            if (invoiceGrossAmountToUse()==null){
                b.append("no invoice gross amount; ");
            }
            if (invoiceTaxToUse()==null || invoiceTaxToUse().equals("")){
                b.append("no invoice tax; ");
            }
        }

        //charge validation
        List<String> charges = Arrays.asList(
                "PROJECT MANAGEMENT",
                "TAX",
                "WORKS",
                "RELOCATION / DISPOSSESSION INDEMNITY",
                "ARCHITECT / GEOMETRICIAN FEES",
                "LEGAL / BAILIFF FEES",
                "MARKETING",
                "TENANT INSTALLATION WORKS",
                "SECURITY AGENTS",
                "LETTING FEES",
                "OTHER"
        );
        if (getCharge()!=null && !charges.contains(getCharge())){
            b.append("charge unknown; ");
        }

        //tax validation
        List<String> taxcodes = Arrays.asList(
                "FRA",
                "FRC",
                "FRD",
                "FRE",
                "FRF",
                "FRO",
                "FRR",
                "FRS",
                "GBR-VATSTD",   // added for Open source version, integ testing
                "NLD-VATSTD"
        );
        if (taxToUse()!=null && !taxcodes.contains(taxToUse())){
            b.append("tax unknown; ");
        }
        if (invoiceTaxToUse()!=null && !taxcodes.contains(invoiceTaxToUse())){
            b.append("invoice tax unknown; ");
        }

        //period validation
        if (getPeriod()!=null && !getPeriod().matches("F\\d{4}")){
            b.append("period unknown; ");
        }
        return b.length()==0 ? "OK" : b.toString();
    }

    @Override
    public void handleRow(final OrderInvoiceImportHandler previousRow) {

            if(executionContext != null && excelFixture2 != null) {
                executionContext.addResult(excelFixture2,this.handle(previousRow));
            }

    }

}

