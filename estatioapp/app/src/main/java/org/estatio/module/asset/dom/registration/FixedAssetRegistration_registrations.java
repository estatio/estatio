package org.estatio.module.asset.dom.registration;

import org.apache.isis.applib.annotation.*;
import org.estatio.module.asset.dom.FixedAsset;

import javax.inject.Inject;
import java.util.List;

@Mixin(method = "coll")
public class FixedAssetRegistration_registrations {

    private final FixedAsset subject;

    public FixedAssetRegistration_registrations(FixedAsset subject) {
        this.subject = subject;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @MemberOrder(
            name = "Registrations",
            sequence = "13.5"
    )
    public List<FixedAssetRegistration> coll() {
        return ccoRegistrationContributions.registrations(this.subject);
    }

    @Inject
    FixedAssetService ccoRegistrationContributions;
}
