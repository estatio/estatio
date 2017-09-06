package org.estatio.capex.dom.payment.export;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.dom.invoice.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@ViewModel()
@Getter @Setter
public class PaymentLineExportV1 {

    public PaymentLineExportV1(final PaymentLine paymentLine){
        final PaymentBatch batch = paymentLine.getBatch();
        final IncomingInvoice invoice = paymentLine.getInvoice();

        this.ReportingCode = invoice.getPaymentMethod().equals(PaymentMethod.DIRECT_DEBIT) ? "CP_DIRECTDEBIT_INVOICES" : "CP_INVOICES";
        this.yrperiod = toYearPeriod(invoice.getDateReceived());
        this.Element_1 = invoice.getBuyer().getReference().concat(invoice.getCurrency().getReference());
        this.Bank_Account_Code = batch == null ? "" : batch.getDebtorBankAccount().getExternalReference();
        this.Date_Invoice = invoice.getInvoiceDate();
        this.DataPayment = batch == null ? null : batch.getRequestedExecutionDate().toLocalDate();
        this.Supplier_Name = invoice.getSeller().getName();
        this.Supplier_Name_2 = "";
        this.Cost_Centre = invoice.getProperty() == null ? "" : invoice.getProperty().getExternalReference();
        this.Description = "";
        this.Amount_Net = invoice.getNetAmount();
        this.Gross_Amount = invoice.getGrossAmount();
        this.TVA = invoice.getVatAmount();
        this.inpdate = batch.getCreatedOn().toLocalDate();
    }

    private int toYearPeriod(final LocalDate date) {
        return date.getYear()*100+ date.getMonthOfYear();
    }

    // CP_INVOICES
    @MemberOrder(sequence = "1") @Nullable
    private String ReportingCode;

    // 201801
    @MemberOrder(sequence = "2") @Nullable
    private int yrperiod;

    // FR01EUR
    @MemberOrder(sequence = "3") @Nullable
    private String Element_1;

    // FR01BAING174
    @MemberOrder(sequence = "4") @Nullable
    private String Bank_Account_Code;

    // 10-Jul-17
    @MemberOrder(sequence = "5") @Nullable
    private LocalDate Date_Invoice;

    // 10-Jul-17
    @MemberOrder(sequence = "6") @Nullable
    private LocalDate DataPayment;

    // FETE SENSATION
    @MemberOrder(sequence = "7") @Nullable
    private String Supplier_Name;

    // RC-FETESENSAT
    @MemberOrder(sequence = "8") @Nullable
    private String Supplier_Name_2;

    // FRRCHA1
    @MemberOrder(sequence = "9") @Nullable
    private String Cost_Centre;

    // R-SDD0
    @MemberOrder(sequence = "10") @Nullable
    private String Description;

    // 27.592,73
    @MemberOrder(sequence = "11") @Nullable
    private BigDecimal Amount_Net;

    // 5.518,56
    @MemberOrder(sequence = "12") @Nullable
    private BigDecimal TVA;

    // 33.111,29
    @MemberOrder(sequence = "13") @Nullable
    private BigDecimal Gross_Amount;

    // 26 Jun 2017
    @MemberOrder(sequence = "14") @Nullable
    private LocalDate inpdate;
}
