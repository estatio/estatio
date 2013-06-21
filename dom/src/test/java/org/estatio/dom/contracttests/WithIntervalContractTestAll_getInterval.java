package org.estatio.dom.contracttests;

import com.google.common.collect.ImmutableMap;

import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalContractTestAbstract_getInterval;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceItemForTesting;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTesting;


/**
 * Automatically tests all domain objects implementing {@link WithInterval}.
 */
public class WithIntervalContractTestAll_getInterval extends WithIntervalContractTestAbstract_getInterval {

    public WithIntervalContractTestAll_getInterval() {
        super(Constants.packagePrefix, ImmutableMap.<Class<?>,Class<?>>of(
                Agreement.class, AgreementForTesting.class,
                InvoiceItem.class, InvoiceItemForTesting.class,
                LeaseTerm.class, LeaseTermForTesting.class)
                );
    }

}
