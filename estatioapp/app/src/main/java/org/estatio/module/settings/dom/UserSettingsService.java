package org.estatio.module.settings.dom;

import java.util.List;

public interface UserSettingsService {

    UserSetting find(String user, String key);
    
    List<UserSetting> listAll();

    List<UserSetting> listAllFor(String user);
}
