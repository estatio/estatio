package org.estatio.module.capex.app.credittransfer;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ViewModel()
@Getter @Setter
@AllArgsConstructor
public class CreditTransferExportLine {

    // transfer information

    @MemberOrder(sequence = "0")
    @Nullable
    private int line;

    @MemberOrder(sequence = "1")
    @Nullable
    private String debtorBankAccount;

    @MemberOrder(sequence = "2")
    @Nullable
    private String paymentBatchCreatedOn;

    @MemberOrder(sequence = "3")
    @Nullable
    private String paymentId;

    @MemberOrder(sequence = "4")
    @Nullable
    private String sellerBankAccount;

    @MemberOrder(sequence = "5")
    @Nullable
    private String newIban;

    @MemberOrder(sequence = "6")
    @Nullable
    private String sellerName;

    @MemberOrder(sequence = "7")
    @Nullable
    private String sellerReference;

    @MemberOrder(sequence = "8")
    @Nullable
    private BigDecimal paymentAmount;

    @MemberOrder(sequence = "9")
    @Nullable
    private String currency;

    // invoice information

    @MemberOrder(sequence = "10")
    @Nullable
    private String invoiceNumber;

    @MemberOrder(sequence = "11")
    @Nullable
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "12")
    private BigDecimal invoiceGrossAmount;

    @MemberOrder(sequence = "13")
    private String approvals;

    @MemberOrder(sequence = "14")
    private String invoiceDescriptionSummary;

    @MemberOrder(sequence = "17")
    private String invoiceDocumentName;

    @MemberOrder(sequence = "15")
    private String invoiceType;

    @MemberOrder(sequence = "16")
    @Nullable
    private String property;

}
