/**
 * Defines {@link org.estatio.dom.financial.FinancialAccount}s, each of which is
 * {@link org.estatio.dom.financial.FinancialAccount#getOwner() owned} by a {@link org.estatio.module.party.dom.Party}.
 *
 * <p>
 * {@link org.estatio.dom.financial.FinancialAccount} is abstract, with 
 * {@link org.estatio.dom.financial.FinancialAccountType} acts as a powertype.  Currently only a single concrete 
 * subtype, {@link org.estatio.dom.financial.bankaccount.BankAccount}, exists.
 * 
 * <p>
 * {@link org.estatio.dom.financial.bankaccount.BankAccountType} characterises the type of the bank account, for example
 * <i>CHECKING</i>.
 * 
 * <p>
 * A {@link org.estatio.module.party.dom.Party} is required to have a {@link org.estatio.dom.financial.bankaccount.BankAccount}.
 */
package org.estatio.dom.financial;