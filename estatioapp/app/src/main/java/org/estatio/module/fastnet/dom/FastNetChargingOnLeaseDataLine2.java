package org.estatio.module.fastnet.dom;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ViewModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@ViewModel
@Getter @Setter
@AllArgsConstructor
public class FastNetChargingOnLeaseDataLine2 {

        // keys 3
        private String keyToLeaseExternalReference;

        private String keyToChargeReference;

        private LocalDate exportDate;

        // charging 12

        private String kontraktNr;

        private String kundNr;

        private String kod;

        private String kod2;

        private String kontText;

        private String kontText2;

        private String fromDat;

        private String tomDat;

        private String debPer;

        private String firstPosStart;

        private BigDecimal arsBel;

        private LocalDate applied;

        // lease 9

        private String leaseReference;

        private String externalReference;

        private String tenantName;

        private String tenantReference;

        private String leaseStatus;

        private LocalDate tenancyStartDate;

        private LocalDate tenancyEndDate;

        private LocalDate leaseStartDate;

        private LocalDate leaseEndDate;

        // lease item 5

        private String leaseItemType;

        private String invoicingFrequency;

        private LocalDate leaseItemStartDate;

        private LocalDate leaseItemEndDate;

        private String chargeReference;

        // lease term 7

        private LocalDate leaseTermStartDate;

        private LocalDate leaseTermEndDate;

        private String leaseTermStatus;

        private BigDecimal baseValue;

        private BigDecimal settledValue;

        private BigDecimal value;

        private BigDecimal budgetedValue;

}
