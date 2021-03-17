package org.estatio.module.lease.dom.party;

import lombok.Getter;

public enum AdministrationStatus {

    SAFEGUARD_PLAN("Plan de Sauvegarde"),
    LEGAL_REDRESS("Redressement Judiciaire"),
    LIQUIDATION("Liquidation");

    AdministrationStatus(final String description) {
        this.description = description;
    }

    @Getter
    private String description;

}
