/**
 * Defines {@link org.estatio.module.financial.dom.FinancialAccount}s, each of which is
 * {@link org.estatio.module.financial.dom.FinancialAccount#getOwner() owned} by a {@link org.estatio.module.party.dom.Party}.
 *
 * <p>
 * {@link org.estatio.module.financial.dom.FinancialAccount} is abstract, with
 * {@link org.estatio.module.financial.dom.FinancialAccountType} acts as a powertype.  Currently only a single concrete
 * subtype, {@link org.estatio.module.financial.dom.BankAccount}, exists.
 * 
 * <p>
 * {@link org.estatio.module.financial.dom.BankAccountType} characterises the type of the bank account, for example
 * <i>CHECKING</i>.
 * 
 * <p>
 * A {@link org.estatio.module.party.dom.Party} is required to have a {@link org.estatio.module.financial.dom.BankAccount}.
 */
package org.estatio.module.financial.dom;