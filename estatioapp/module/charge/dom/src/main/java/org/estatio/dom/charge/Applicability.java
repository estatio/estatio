package org.estatio.dom.charge;

public enum Applicability {
    OUTGOING,
    INCOMING,
    IN_AND_OUT;
    
    public boolean supportsIncoming() {
        return this == INCOMING || this == IN_AND_OUT; 
    }
    public boolean supportsOutgoing() {
        return this == OUTGOING || this == IN_AND_OUT;
    }

    public static class Meta {
        private Meta(){}
        public static final int MAX_LEN = 10; // max length of this enum
    }
}
