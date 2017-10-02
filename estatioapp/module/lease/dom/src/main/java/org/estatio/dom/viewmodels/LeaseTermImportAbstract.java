package org.estatio.dom.viewmodels;

import java.math.BigInteger;

import org.joda.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

public class LeaseTermImportAbstract {

    // leaseItem fields
    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String itemTypeName;

    @Getter @Setter
    private BigInteger itemSequence;

    @Getter @Setter
    private LocalDate itemStartDate;

    @Getter @Setter
    private LocalDate itemNextDueDate;

    @Getter @Setter
    private String itemChargeReference;

    @Getter @Setter
    private LocalDate itemEpochDate;

    @Getter @Setter
    private String itemInvoicingFrequency;

    @Getter @Setter
    private String itemPaymentMethod;

    @Getter @Setter
    private String itemStatus;

    @Getter @Setter
    private String leaseItemAtPath;

    // generic term fields
    @Getter @Setter
    private BigInteger sequence;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String status;

    void process(){


    }

}
