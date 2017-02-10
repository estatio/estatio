/**
 * Defines {@link org.estatio.financial.dom.FinancialAccount}s, each of which is
 * {@link org.estatio.financial.dom.FinancialAccount#getOwner() owned} by a {@link org.estatio.dom.party.Party}.
 *
 * <p>
 * {@link org.estatio.financial.dom.FinancialAccount} is abstract, with
 * {@link org.estatio.financial.dom.FinancialAccountType} acts as a powertype.  Currently only a single concrete
 * subtype, {@link org.estatio.financial.dom.bankaccount.BankAccount}, exists.
 * 
 * <p>
 * {@link org.estatio.financial.dom.bankaccount.BankAccountType} characterises the type of the bank account, for example
 * <i>CHECKING</i>.
 * 
 * <p>
 * A {@link org.estatio.dom.party.Party} is required to have a {@link org.estatio.financial.dom.bankaccount.BankAccount}.
 */
package org.estatio.financial.dom;