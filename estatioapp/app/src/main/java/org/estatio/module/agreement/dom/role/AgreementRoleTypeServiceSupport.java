package org.estatio.module.agreement.dom.role;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Each module that contributes a set of {@link IAgreementRoleType}s should implement this SPI service.
 */
public interface AgreementRoleTypeServiceSupport {

    @Programmatic
    List<IAgreementRoleType> listAll();
}
