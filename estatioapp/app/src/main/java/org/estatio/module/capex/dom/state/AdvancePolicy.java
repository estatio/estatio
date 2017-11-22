package org.estatio.module.capex.dom.state;

public enum AdvancePolicy {

    AUTOMATIC,
    MANUAL;

    public boolean isManual() { return this == MANUAL; }
    public boolean isAutomatic() { return this == AUTOMATIC; }

}
