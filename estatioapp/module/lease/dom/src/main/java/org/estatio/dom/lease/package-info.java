/**
 * Defines the central {@link org.estatio.dom.lease.Lease} entity and associated entities.
 * 
 * <p>
 * {@link org.estatio.dom.lease.Lease} is a subtype of {@link org.estatio.agreement.dom.Agreement} and thus
 * associates the {@link org.estatio.dom.party.Party} that is the <i>TENANT</i> and the 
 * {@link org.estatio.dom.party.Party} that is the <i>LANDLORD</i>).  Each {@link org.estatio.dom.lease.Lease}
 * {@link Lease#getItems() contains} a number of {@link org.estatio.dom.lease.LeaseItem}s, being the elements that the
 * tenant is, in effect, purchasing.  There are three {@link org.estatio.dom.lease.LeaseItemType type}s of lease item,
 * {@link org.estatio.dom.lease.LeaseItemType#RENT (indexable) rent}, 
 * {@link org.estatio.dom.lease.LeaseItemType#TURNOVER_RENT turnover rent} and
 * {@link org.estatio.dom.lease.LeaseItemType#SERVICE_CHARGE service charge}.
 * 
 * <p>
 * The {@link org.estatio.dom.lease.LeaseItem}s in turn {@link org.estatio.dom.lease.LeaseItem#getTerms() aggregate}
 * {@link org.estatio.dom.lease.LeaseTerm}s, typically per quarter.  There are subclasses of 
 * {@link org.estatio.dom.lease.LeaseTerm} for each of the {@link org.estatio.dom.lease.LeaseItemType type}s; so the 
 * type is an (indirect sort of) power-type.  The subtypes are, respectively,  
 * {@link org.estatio.dom.lease.LeaseTermForIndexable lease term for indexable rent lease term}, 
 * {@link org.estatio.dom.lease.LeaseTermForTurnoverRent lease term for turnover rent} and
 * {@link org.estatio.dom.lease.LeaseTermForServiceCharge lease term for service charge}. 
 *
 * <p>
 * The {@link org.estatio.dom.lease.Occupancy} entity associates the {@link org.estatio.dom.lease.Lease} with a
 * particular {@link org.estatio.asset.dom.Unit}.
 * 
 * <p>
 * {@link org.estatio.dom.lease.Lease} also integrates with the <tt>invoicing</tt> module, by implementing the
 * {@link org.estatio.invoice.dom.InvoiceSource} interface.  In practice each {@link org.estatio.dom.lease.LeaseTerm}
 * corresponds to an {@link org.estatio.invoice.dom.InvoiceItem}... the bill for a particular service for a particular
 * period of time.
 * 
 * <p>
 * An important philosophy is that {@link org.estatio.invoice.dom.InvoiceItem}s become immutable once invoiced.  If
 * there is a retrospective change in reference data (eg {@link org.estatio.index.dom.Index indices} which impact the
 * {@link org.estatio.dom.lease.LeaseTermForIndexable}, then the invoice calculations for that term can be re-run. 
 * This may produce a delta {@link org.estatio.invoice.dom.InvoiceItem} (debit or credit), which is billed in arrears.
 *  
 * <p>
 * As already noted, {@link org.estatio.dom.lease.Lease} is a particular (sub)type of 
 * {@link org.estatio.agreement.dom.Agreement}, the other one being {@link org.estatio.bankmandate.dom.BankMandate}.
 * Thus, two parties will often have two related but independent {@link org.estatio.agreement.dom.Agreement}s.  Each
 * lease {@link org.estatio.dom.lease.Lease#getPaidBy() tracks} the {@link org.estatio.bankmandate.dom.BankMandate} by
 * which its invoices are paid.
 */
package org.estatio.dom.lease;