package org.estatio.module.capex.app.paymentline;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.app.paymentline.PaymentLineForExcelExportV1"
)
@Getter @Setter
@AllArgsConstructor
public class PaymentLineForExcelExportV1 {

    @MemberOrder(sequence = "1") @Nullable
    private String debtorBankAccount;

    @MemberOrder(sequence = "2") @Nullable
    private LocalDate invoiceDate;

    @MemberOrder(sequence = "4") @Nullable
    private LocalDate paymentDate;

    @MemberOrder(sequence = "5") @Nullable
    private String sellerName;

    @MemberOrder(sequence = "6") @Nullable
    private String sellerReference;

    @MemberOrder(sequence = "7") @Nullable
    private String propertyReference;

    @MemberOrder(sequence = "8") @Nullable
    private String invoiceNumber;

    @MemberOrder(sequence = "9") @Nullable
    private String invoiceId;

    @MemberOrder(sequence = "10") @Nullable
    private BigDecimal invoiceNetAmount;

    @MemberOrder(sequence = "11") @Nullable
    private BigDecimal invoiceVatAmount;

    @MemberOrder(sequence = "12") @Nullable
    private BigDecimal invoiceGrossAmount;

}
