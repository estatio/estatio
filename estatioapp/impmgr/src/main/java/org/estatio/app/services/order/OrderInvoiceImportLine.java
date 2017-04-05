package org.estatio.app.services.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
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
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private int rowCounter;
    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String status;
    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String orderNumber;
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String charge;
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate entryDate;
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private LocalDate orderDate;
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private String seller;
    @Getter @Setter
    @MemberOrder(sequence = "7")
    private String orderDescription;
    @Getter @Setter
    @MemberOrder(sequence = "8")
    @Column(scale = 2)
    private BigDecimal netAmount;
    @Getter @Setter
    @MemberOrder(sequence = "9")
    @Column(scale = 2)
    private BigDecimal vatAmount;
    @Getter @Setter
    @MemberOrder(sequence = "10")
    @Column(scale = 2)
    private BigDecimal grossAmount;
    @Getter @Setter
    @MemberOrder(sequence = "11")
    private String orderApprovedBy;
    @Getter @Setter
    @MemberOrder(sequence = "12")
    private String orderApprovedOn;
    @Getter @Setter
    @MemberOrder(sequence = "13")
    private String projectReference;
    @Getter @Setter
    @MemberOrder(sequence = "14")
    private String period;
    @Getter @Setter
    @MemberOrder(sequence = "15")
    private String tax;
    @Getter @Setter
    @MemberOrder(sequence = "16")
    private String invoiceNumber;
    @Getter @Setter
    @MemberOrder(sequence = "17")
    private String invoiceDescription;
    @Getter @Setter
    @MemberOrder(sequence = "18")
    @Column(scale = 2)
    private BigDecimal invoiceNetAmount;
    @Getter @Setter
    @MemberOrder(sequence = "19")
    @Column(scale = 2)
    private BigDecimal invoiceVatAmount;
    @Getter @Setter
    @MemberOrder(sequence = "20")
    @Column(scale = 2)
    private BigDecimal invoiceGrossAmount;
    @Getter @Setter
    @MemberOrder(sequence = "21")
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

        if (previousRow==null) {
            setRowCounter(1);
        } else {
            setRowCounter(previousRow.getRowCounter() + 1);

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

        OrderInvoiceImportLine lineItem = null;

        if (getEntryDate()!=null || invoiceNumberToUse()!=null) {
            lineItem = new OrderInvoiceImportLine(
                    getRowCounter(),
                    validateRow(),
                    clean(getOrderNumber()),
                    getCharge(),
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
                    invoiceTaxToUse(),
                    null,
                    null
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
        if (getNetAmount()!= null && taxToUse().equals("FRE")){
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
                "LEGAL FEES",
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
                "FRS"
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
    public void handleRow(final OrderInvoiceImportLine previousRow) {

            if(executionContext != null && excelFixture2 != null) {
                executionContext.addResult(excelFixture2,this.handle(previousRow));
            }

    }

}

