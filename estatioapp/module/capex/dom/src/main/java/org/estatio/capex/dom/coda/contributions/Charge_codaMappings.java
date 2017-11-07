package org.estatio.capex.dom.coda.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.charge.dom.Charge;

/**
 * This cannot be inlined (needs to be a mixin) because Charge does not know about Coda.
 */
@Mixin
public class Charge_codaMappings {

    private final Charge charge;

    public Charge_codaMappings(Charge charge) {
        this.charge = charge;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<CodaMapping> $$() {
        return repository.findByCharge(charge);
    }

    @Inject CodaMappingRepository repository;

}
