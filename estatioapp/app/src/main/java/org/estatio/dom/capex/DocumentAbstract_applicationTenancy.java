package org.estatio.dom.capex;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;

@Mixin(method = "prop")
public class DocumentAbstract_applicationTenancy {

    private final DocumentAbstract documentAbstract;

    public DocumentAbstract_applicationTenancy(final DocumentAbstract documentAbstract) {
        this.documentAbstract = documentAbstract;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public ApplicationTenancy prop() {
        return applicationTenancyRepository.findByPath(documentAbstract.getAtPath());
    }

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
