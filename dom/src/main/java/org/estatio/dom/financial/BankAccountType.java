package org.estatio.dom.financial;

public enum BankAccountType {

    DEPOSIT("Deposit"), CHECKING("Checking");

    private String title;

    private BankAccountType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
