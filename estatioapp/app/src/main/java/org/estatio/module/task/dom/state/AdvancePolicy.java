package org.estatio.module.task.dom.state;

public enum AdvancePolicy {

    AUTOMATIC,
    MANUAL;

    public boolean isManual() { return this == MANUAL; }
    public boolean isAutomatic() { return this == AUTOMATIC; }

}
