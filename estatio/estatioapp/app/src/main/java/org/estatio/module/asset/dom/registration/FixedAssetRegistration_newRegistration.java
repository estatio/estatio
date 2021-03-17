package org.estatio.module.asset.dom.registration;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.estatio.module.asset.dom.FixedAsset;

import javax.inject.Inject;

@Mixin(method = "act")
public class FixedAssetRegistration_newRegistration {

    private final FixedAsset subject;

    public FixedAssetRegistration_newRegistration(FixedAsset subject) {
        this.subject = subject;
    }

    @Action(
            semantics =  SemanticsOf.NON_IDEMPOTENT
    )
    @MemberOrder(
            name = "Registrations",
            sequence = "13"
    )
    public FixedAssetRegistration act(
            final FixedAssetRegistrationType type) {
        return fixedAssetService.newRegistration(this.subject, type);
    }

    @Inject
    FixedAssetService fixedAssetService;
}
