package org.estatio.module.coda.app;

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
                "FR-GEN", "RA-PROF", "MV-DIV", "FR-ART17", "NCR-GEN", "NCR-ART17", "FR-GEN01", "NCR-GEN01"
        );
    }

}
