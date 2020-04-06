package org.estatio.module.coda.app;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CodaCmpCodeService {

    @Programmatic
    public List<String> listAll() {
        return Arrays.asList(
                "IT01", "IT04", "IT05", "IT07", "IT08", "IT09", "IT10", "IT11", "IT12"
        );
    }

    @Programmatic
    public List<String> noOrderValidation(){
        return Arrays.asList(
                "IT07"
        );
    }

    @Programmatic
    public List<String> noProjectValidation(){
        return Arrays.asList(
                "IT07"
        );
    }


}
