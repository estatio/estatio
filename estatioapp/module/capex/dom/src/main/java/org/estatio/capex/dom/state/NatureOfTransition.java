package org.estatio.capex.dom.state;

enum NatureOfTransition {
    EXPLICIT,
    AUTOMATIC;

    public static NatureOfTransition fromIntent(final boolean isExplicitAction) {
        return isExplicitAction ? EXPLICIT : AUTOMATIC;
    }
}
