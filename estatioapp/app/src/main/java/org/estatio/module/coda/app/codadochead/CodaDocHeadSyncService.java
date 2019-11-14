package org.estatio.module.coda.app.codadochead;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "codadochead.CodaDocHeadSyncService"
)
public class CodaDocHeadSyncService {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void retrieveCodaDoc(
            final String cmpCode,
            final String docCode,
            final int docNum) {
        // no-op, just for the interaction to be picked up by camel
    }

}
