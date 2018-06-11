package org.estatio.module.capex.app.credittransfer;

import java.math.BigDecimal;

import javax.annotation.Nullable;

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

    @MemberOrder(sequence = "1")
    @Nullable
    private int line;

    @MemberOrder(sequence = "2")
    @Nullable
    private String debtorBankAccount;

    @MemberOrder(sequence = "3")
    @Nullable
    private String sellerBankAccount;

    @MemberOrder(sequence = "4")
    @Nullable
    private String newIban;

    @MemberOrder(sequence = "5")
    @Nullable
    private String sellerName;

    // invoice information
    @MemberOrder(sequence = "6")
    @Nullable
    private String invoiceNumber;

    @MemberOrder(sequence = "7")
    private BigDecimal invoiceGrossAmount;

    @MemberOrder(sequence = "8")
    private String approvals;

    @MemberOrder(sequence = "9")
    private String invoiceDescriptionSummary;

    @MemberOrder(sequence = "10")
    private String invoiceType;

    @MemberOrder(sequence = "11")
    @Nullable
    private String property;

    @MemberOrder(sequence = "12")
    private String invoiceDocumentName;

}
