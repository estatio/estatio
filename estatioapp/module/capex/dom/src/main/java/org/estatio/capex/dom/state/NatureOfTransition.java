package org.estatio.capex.dom.state;

public enum NatureOfTransition {
    EXPLICIT,
    AUTOMATIC;

    public static NatureOfTransition fromIntent(final boolean isExplicitAction) {
        return isExplicitAction ? EXPLICIT : AUTOMATIC;
    }
}
