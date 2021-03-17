package org.incode.platform.dom.alias.integtests.dom.alias.dom.spiimpl.apptenancy;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.alias.dom.spi.ApplicationTenancyRepository;

/**
 * Mandatory implementation of the {@link ApplicationTenancyRepository} SPI.
 */
@DomainService(
    nature = NatureOfService.DOMAIN
)
public class ApplicationTenancyDemoEnumRepository implements ApplicationTenancyRepository {

    @Override
    public Collection<String> atPathsFor(final Object aliased) {
        return Lists.newArrayList(
                Arrays.stream(ApplicationTenancyDemoEnum.values()).map(ApplicationTenancyDemoEnum::getPath)
                        .collect(Collectors.toList())
        );
    }
}
