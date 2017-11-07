package org.estatio.module.guarantee.dom.contributed;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.lease.dom.Lease;

/**
 * Cannot be inlined (needs to be a mixin) because Lease does not know about guarantees
 */
@Mixin
public class Lease_guarantees {

    private final Lease lease;

    public Lease_guarantees(final Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Guarantee> guarantees() {
        return leaseGuaranteeService.guarantees(lease);
    }

    @Inject
    LeaseGuaranteeService leaseGuaranteeService;


}
