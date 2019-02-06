package org.estatio.module.capex.spiimpl.docs.rml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Developer {

    private final String name;
    private final String lastName;
    private final String mail;

}
