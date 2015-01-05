/**
 * Defines {@link org.estatio.dom.financial.FinancialAccount}s, each of which is
 * {@link org.estatio.dom.financial.FinancialAccount#getOwner() owned} by a {@link org.estatio.dom.party.Party}.
 * Also defines {@link org.estatio.dom.bankmandate.BankMandate}, being a means by which one party can pay another (in
 * other words, a direct debit.
 * 
 * <p>
 * {@link org.estatio.dom.bankmandate.BankMandate} is a particular (sub)type of
 * {@link org.estatio.dom.agreement.Agreement}, the other one being <tt>Lease</tt>.  Thus, two parties will often have
 * two related but independent {@link org.estatio.dom.agreement.Agreement}s, one being a <tt>BankMandate</tt>, the
 * other the <tt>Lease</tt>.  
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
 * A {@link org.estatio.dom.party.Party} is required to have a 
 * {@link org.estatio.dom.financial.bankaccount.BankAccount} in order that a {@link org.estatio.dom.bankmandate.BankMandate} can
 * be created against it. 
 */
package org.estatio.dom.financial;