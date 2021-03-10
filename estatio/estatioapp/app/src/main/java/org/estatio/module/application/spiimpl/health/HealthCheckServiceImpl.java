package org.estatio.module.application.spiimpl.health;

import javax.inject.Inject;

import com.google.common.base.Throwables;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.health.Health;
import org.apache.isis.applib.services.health.HealthCheckService;

import org.estatio.module.asset.dom.PropertyRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class HealthCheckServiceImpl implements HealthCheckService {

    @Override
    public Health check() {
        try {
            propertyRepository.ping();
            return Health.ok();
        } catch(Exception ex) {
            return Health.error(Throwables.getStackTraceAsString(ex));
        }
    }

    @Inject
    PropertyRepository propertyRepository;

}
