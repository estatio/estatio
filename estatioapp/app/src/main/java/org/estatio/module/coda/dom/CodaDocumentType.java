package org.estatio.module.coda.dom;

public enum CodaDocumentType {
    INITIAL_COVID_AMORTISATION, // one time only journal entry capitalising the discount (based on scheduled value)
                                // NOTE: is not created for Amendment Based Amortisation Schedule
    RECURRING_COVID_AMORTISATION // recurring journal entry depreciating the capitalised amount (based on entry amount)
}
