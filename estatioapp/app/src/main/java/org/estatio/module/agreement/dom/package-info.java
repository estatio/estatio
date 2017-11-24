/**
 * Defines {@link org.estatio.module.agreement.dom.Agreement}, which associates a number of
 * {@link org.estatio.module.party.dom.Party party} playing various {@link org.estatio.module.agreement.dom.AgreementRole role}s.
 * 
 * <p>
 * Agreement itself is abstract, with its associated {@link org.estatio.module.agreement.dom.type.AgreementType type} acting
 * as a powertype to the subclass.  The type subtypes are <tt>Lease</tt> (representing the occupancy of a unit in 
 * a property) and <tt>BankMandate</tt> (a mechanism by which one party pays another for services).  Thus, two parties 
 * will often have two related but independent {@link org.estatio.module.agreement.dom.Agreement}s, one being a
 * <tt>BankMandate</tt>, the other the <tt>Lease</tt>.  

 * <p>
 * Typical roles are <i>PROPERTY_OWNER</i> or <i>ASSET_MANAGER</i> (for <tt>Lease</tt> agreements, say).
 * The party acting in a given role can change over time.
 *
 * <p>
 * Every {@link org.estatio.module.agreement.dom.Agreement} has a
 * {@link org.estatio.module.agreement.dom.Agreement#getPrimaryParty() primary} and a
 * {@link org.estatio.module.agreement.dom.Agreement#getSecondaryParty() secondary} party; which role identifies these
 * is dependent on the {@link org.estatio.module.agreement.dom.Agreement#getType() type}.
 */
package org.estatio.module.agreement.dom;