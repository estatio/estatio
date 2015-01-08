package org.estatio.dom.financial;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.JdoColumnScale;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name = "findByFinancialAccount",
                language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.financial.FinancialAccountTransaction "
                        + "WHERE financialAccount == :financialAccount"),
        @Query(
                name = "findByFinancialAccountAndTransactionDate",
                language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.financial.FinancialAccountTransaction "
                        + "WHERE financialAccount == :financialAccount && "
                        + "transactionDate == :transactionDate"),
        @Query(
                name = "findByFinancialAccountAndTransactionDateAndSequence",
                language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.financial.FinancialAccountTransaction "
                        + "WHERE financialAccount == :financialAccount && "
                        + "transactionDate == :transactionDate && "
                        + "sequence == :sequence")
})
@Index(
        name = "FinancialAccountTransaction_financialAccount_transactionDate_IDX",
        members = { "financialAccount", "transactionDate" })
@Unique(
        name = "FinancialAccountTransaction_financialAccount_transactionDate_sequence_UNQ",
        members = { "financialAccount", "transactionDate" })
public class FinancialAccountTransaction extends EstatioDomainObject<FinancialAccountTransaction> {

    public FinancialAccountTransaction() {
        super("financialAccount,transactionDate,description,amount");
        // TODO Auto-generated constructor stub
    }

    // //////////////////////////////////////

    FinancialAccount financialAccount;

    @Column(name = "financialAccountId")
    @MemberOrder(sequence = "1")
    public FinancialAccount getFinancialAccount() {
        return financialAccount;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    // //////////////////////////////////////

    LocalDate transactionDate;

    @Column(allowsNull = "false")
    @MemberOrder(sequence = "2")
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(final LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @Column(allowsNull = "false")
    @Hidden
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    // //////////////////////////////////////

    String description;

    @Column(allowsNull = "true", length = JdoColumnLength.DESCRIPTION)
    @MemberOrder(sequence = "4")
    @Hidden(where = Where.ALL_TABLES)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    BigDecimal amount;

    @Column(allowsNull = "false", scale = JdoColumnScale.MONEY)
    @MemberOrder(sequence = "5")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
