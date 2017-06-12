package org.estatio.dom.agreement.role;

import java.util.List;

/**
 * Each module that contributes a set of {@link IAgreementRoleType}s should implement this SPI service.
 */
public interface AgreementRoleTypeServiceSupport {

    List<IAgreementRoleType> listAll();
}
