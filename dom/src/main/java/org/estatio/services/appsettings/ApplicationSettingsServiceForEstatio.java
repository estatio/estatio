package org.estatio.services.appsettings;

import java.util.List;

import org.estatio.dom.ApplicationSettingCreator;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingJdo;
import org.apache.isis.objectstore.jdo.applib.service.settings.ApplicationSettingsServiceJdo;

@Hidden
public class ApplicationSettingsServiceForEstatio extends ApplicationSettingsServiceJdo  {


    @Override
    @Hidden
    public ApplicationSetting find(@Named("Key") String key) {
        installDefaultsIfRequired();
        return super.find(key);
    }

    @Override
    @MemberOrder(sequence = "1")
    public List<ApplicationSetting> listAll() {
        installDefaultsIfRequired();
        return super.listAll();
    }

    
    private boolean installedDefaults;
    private void installDefaultsIfRequired() {
        // horrid, but cannot use @PostConstruct since no container injected, and no Isis session available
        if(!installedDefaults) {
            installedDefaults = true;
            installDefaults();
        }
    }

    // @PostConstruct
    private void installDefaults() {
        createSettingsIfRequired(org.estatio.dom.ApplicationSettingKey.values());
        createSettingsIfRequired(org.estatio.dom.lease.ApplicationSettingKey.values());
        createSettingsIfRequired(org.estatio.dom.invoice.ApplicationSettingKey.values());
    }

    private void createSettingsIfRequired(ApplicationSettingCreator[] values) {
        for(org.estatio.dom.ApplicationSettingCreator sd: values) {
            create(sd);
        }
    }

    private void create(org.estatio.dom.ApplicationSettingCreator sd) {
        ApplicationSetting find = find(sd.name());
        if(find == null) {
            sd.create(this);
        }
    }

}

