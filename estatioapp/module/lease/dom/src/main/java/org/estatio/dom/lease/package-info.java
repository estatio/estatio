/**
 * Defines the central {@link org.estatio.module.lease.dom.Lease} entity and associated entities.
 * 
 * <p>
 * {@link org.estatio.module.lease.dom.Lease} is a subtype of {@link org.estatio.dom.agreement.Agreement} and thus
 * associates the {@link org.estatio.module.party.dom.Party} that is the <i>TENANT</i> and the
 * {@link org.estatio.module.party.dom.Party} that is the <i>LANDLORD</i>).  Each {@link org.estatio.module.lease.dom.Lease}
 * {@link Lease#getItems() contains} a number of {@link org.estatio.module.lease.dom.LeaseItem}s, being the elements that the
 * tenant is, in effect, purchasing.  There are three {@link org.estatio.module.lease.dom.LeaseItemType type}s of lease item,
 * {@link org.estatio.module.lease.dom.LeaseItemType#RENT (indexable) rent},
 * {@link org.estatio.module.lease.dom.LeaseItemType#TURNOVER_RENT turnover rent} and
 * {@link org.estatio.module.lease.dom.LeaseItemType#SERVICE_CHARGE service charge}.
 * 
 * <p>
 * The {@link org.estatio.module.lease.dom.LeaseItem}s in turn {@link org.estatio.module.lease.dom.LeaseItem#getTerms() aggregate}
 * {@link org.estatio.module.lease.dom.LeaseTerm}s, typically per quarter.  There are subclasses of
 * {@link org.estatio.module.lease.dom.LeaseTerm} for each of the {@link org.estatio.module.lease.dom.LeaseItemType type}s; so the
 * type is an (indirect sort of) power-type.  The subtypes are, respectively,  
 * {@link org.estatio.module.lease.dom.LeaseTermForIndexable lease term for indexable rent lease term},
 * {@link org.estatio.module.lease.dom.LeaseTermForTurnoverRent lease term for turnover rent} and
 * {@link org.estatio.module.lease.dom.LeaseTermForServiceCharge lease term for service charge}.
 *
 * <p>
 * The {@link org.estatio.module.lease.dom.Occupancy} entity associates the {@link org.estatio.module.lease.dom.Lease} with a
 * particular {@link org.estatio.dom.asset.Unit}.
 * 
 * <p>
 * {@link org.estatio.module.lease.dom.Lease} also integrates with the <tt>invoicing</tt> module, by implementing the
 * {@link org.estatio.dom.invoice.InvoiceSource} interface.  In practice each {@link org.estatio.module.lease.dom.LeaseTerm}
 * corresponds to an {@link org.estatio.dom.invoice.InvoiceItem}... the bill for a particular service for a particular
 * period of time.
 * 
 * <p>
 * An important philosophy is that {@link org.estatio.dom.invoice.InvoiceItem}s become immutable once invoiced.  If 
 * there is a retrospective change in reference data (eg {@link org.estatio.dom.index.Index indices} which impact the
 * {@link org.estatio.module.lease.dom.LeaseTermForIndexable}, then the invoice calculations for that term can be re-run.
 * This may produce a delta {@link org.estatio.dom.invoice.InvoiceItem} (debit or credit), which is billed in arrears.
 *  
 * <p>
 * As already noted, {@link org.estatio.module.lease.dom.Lease} is a particular (sub)type of
 * {@link org.estatio.dom.agreement.Agreement}, the other one being {@link org.estatio.dom.bankmandate.BankMandate}.  
 * Thus, two parties will often have two related but independent {@link org.estatio.dom.agreement.Agreement}s.  Each 
 * lease {@link org.estatio.module.lease.dom.Lease#getPaidBy() tracks} the {@link org.estatio.dom.bankmandate.BankMandate} by
 * which its invoices are paid.
 */
package org.estatio.dom.lease;