package org.estatio.module.settings.dom;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;

public interface ApplicationSettingsService {

    @MemberOrder(sequence="1")
    ApplicationSetting find(@ParameterLayout(named = "Key") String key);

    @MemberOrder(sequence="2")
    List<ApplicationSetting> listAll();

}
