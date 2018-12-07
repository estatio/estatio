package org.estatio.module.application.app;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CodaDocCodeService {

    @Programmatic
    public List<String> listAll() {
        return Arrays.asList(
                "FR-GEN", "RA-PROF"
        );
    }
}
