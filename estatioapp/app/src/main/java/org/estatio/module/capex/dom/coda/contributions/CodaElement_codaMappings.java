package org.estatio.module.capex.dom.coda.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.coda.CodaElement;
import org.estatio.module.capex.dom.coda.CodaMapping;
import org.estatio.module.capex.dom.coda.CodaMappingRepository;

/**
 * TODO: this should be inlined
 */
@Mixin
public class CodaElement_codaMappings {

    private final CodaElement codaElement;

    public CodaElement_codaMappings(CodaElement codaElement) {
        this.codaElement = codaElement;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<CodaMapping> $$() {
        return repository.findByCodaElement(codaElement);
    }

    @Inject CodaMappingRepository repository;

}
