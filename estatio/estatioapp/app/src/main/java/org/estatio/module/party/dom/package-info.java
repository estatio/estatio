/**
 * Defines the {@link org.estatio.module.party.dom.Party party} abstract entity, with concrete subtypes either an
 * {@link org.estatio.module.party.dom.Organisation Organisation} or a {@link org.estatio.module.party.dom.Person Person}.
 * 
 * <p>
 * Any party (though typically organisations) can have {@link org.estatio.module.party.dom.Party#getBankMandates() associated}
 * {@link org.estatio.dom.bankmandate.BankMandate BankMandate}s. 
 * 
 * <p>
 * This module has few dependencies on other modules; rather other modules depend upon it.  Isis' contributed actions
 * and associations are used to enable navigation to other information within Estatio.
 */
package org.estatio.module.party.dom;