package org.estatio.module.budget.dom.budget;

public enum Status {
    NEW,
    ASSIGNED,
    RECONCILING,
    RECONCILED;

    public static class Meta {
        private Meta() {}

        public final static int MAX_LEN = 20;

    }
}
