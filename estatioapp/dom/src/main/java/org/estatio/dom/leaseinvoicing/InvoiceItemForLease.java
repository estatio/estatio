/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.leaseinvoicing;

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceSource;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseTerm;

import lombok.Getter;
import lombok.Setter;

/**
 * A lease-specific subclass of {@link InvoiceItem}, referring
 * {@link #getLeaseTerm() back} to the {@link LeaseTerm} that acts as the
 * <tt>InvoiceSource</tt> of this item's owning {@link Invoice}.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"    // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("InvoiceItemForLease")
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseTerm", language = "JDOQL",
                value = "SELECT " +
                        "FROM InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseTermAndInterval", language = "JDOQL",
                value = "SELECT " +
                        "FROM InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm " +
                        "&& startDate == :startDate " +
                        "&& endDate == :endDate "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseTermAndIntervalAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm " +
                        "&& startDate == :startDate " +
                        "&& endDate == :endDate " +
                        "&& invoice.status == :invoiceStatus"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM InvoiceItemForLease " +
                        "WHERE leaseTerm.leaseItem.lease == :lease " +
                        "&& invoice.status == :invoiceStatus"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM InvoiceItemForLease " +
                        "WHERE leaseTerm.leaseItem == :leaseItem " +
                        "&& invoice.status == :invoiceStatus"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseTermAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm " +
                        "&& invoice.status == :invoiceStatus")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(name = "InvoiceItemForLease_LeaseTerm_StartDate_EndDate_DueDate_IDX",
                members = { "leaseTerm", "startDate", "endDate", "dueDate" }),
        @javax.jdo.annotations.Index(name = "InvoiceItemForLease_LeaseTerm_StartDate_EndDate_IDX",
                members = { "leaseTerm", "startDate", "endDate" }),

})
@DomainObject(editing = Editing.DISABLED)
public class InvoiceItemForLease extends InvoiceItem {

    public String title() {
        return TitleBuilder.start()
                .withParent(getInvoice())
                .withName(getLease())
                .withName(getCharge())
                .toString();
    }

    @Override
    public InvoiceSource getSource() {
        return getLeaseTerm();
    }

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseTermId", allowsNull = "true")
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private LeaseTerm leaseTerm;

    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "false")
    @Property(hidden = Where.PARENTED_TABLES)
    @PropertyLayout(named = "Unit")
    // for the moment, might be generalized (to the user) in the future
    @Getter @Setter
    private FixedAsset fixedAsset;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private Boolean adjustment;

    public final static Ordering<InvoiceItemForLease> ORDERING_BY_LEASE_TERM = new Ordering<InvoiceItemForLease>() {
        public int compare(final InvoiceItemForLease p, final InvoiceItemForLease q) {
            // unnecessary, but keeps findbugs happy...
            if (p == null && q == null) {
                return 0;
            }
            if (p == null) {
                return -1;
            }
            if (q == null) {
                return +1;
            }
            return Ordering.natural().nullsFirst().compare(p.getLeaseTerm(), q.getLeaseTerm());
        }
    };

    @Override
    public int compareTo(final InvoiceItem other) {
        int compare = super.compareTo(other);
        if (compare != 0) {
            return compare;
        }
        if (other instanceof InvoiceItemForLease) {
            return ORDERING_BY_LEASE_TERM.compare(this, (InvoiceItemForLease) other);
        }
        return getClass().getName().compareTo(other.getClass().getName());
    }

    @Inject
    protected AgreementTypeRepository agreementTypeRepository;

    @Inject
    protected AgreementRoleTypeRepository agreementRoleTypeRepository;

}
