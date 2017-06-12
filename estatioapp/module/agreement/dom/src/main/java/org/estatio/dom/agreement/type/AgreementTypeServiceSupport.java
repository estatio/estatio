package org.estatio.dom.agreement.type;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Each module that contributes a set of {@link IAgreementType}s should implement this SPI service.
 */
public interface AgreementTypeServiceSupport {

    @Programmatic
    List<IAgreementType> listAll();

}
