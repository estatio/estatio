package org.estatio.module.capex.app.paymentline;

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
public class PaymentLineForExcelExportV1 {

    @MemberOrder(sequence = "1") @Nullable
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "2") @Nullable
    private LocalDate requestedExecutionDate;

    @MemberOrder(sequence = "3") @Nullable
    private String sellerName;

    @MemberOrder(sequence = "4") @Nullable
    private String sellerReference;

    @MemberOrder(sequence = "5") @Nullable
    private String propertyReference;

    @MemberOrder(sequence = "6") @Nullable
    private String invoiceNumber;

    @MemberOrder(sequence = "7") @Nullable
    private String invoiceId;

    @MemberOrder(sequence = "8") @Nullable
    private BigDecimal invoiceNetAmount;

    @MemberOrder(sequence = "9") @Nullable
    private BigDecimal invoiceVatAmount;

    @MemberOrder(sequence = "10") @Nullable
    private BigDecimal invoiceGrossAmount;

}
