package org.estatio.dom.bankmandate;

public enum SequenceType {

    FIRST(org.estatio.canonical.bankmandate.v1.SequenceType.FIRST),
    RECURRENT(org.estatio.canonical.bankmandate.v1.SequenceType.RECURRENT);

    private final org.estatio.canonical.bankmandate.v1.SequenceType sequenceType;

    SequenceType(final org.estatio.canonical.bankmandate.v1.SequenceType sequenceType) {
        this.sequenceType = sequenceType;
    }

    public org.estatio.canonical.bankmandate.v1.SequenceType forDto() {
        return this.sequenceType;
    }

}
