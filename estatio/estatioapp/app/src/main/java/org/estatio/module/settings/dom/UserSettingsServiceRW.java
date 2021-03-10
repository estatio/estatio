package org.estatio.module.settings.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;

public interface UserSettingsServiceRW extends UserSettingsService {

    @MemberOrder(sequence="11")
    UserSetting newBoolean(
            String user, String name, String description, Boolean defaultValue);

    @MemberOrder(sequence="12")
    UserSetting newString(
            String user, String name, String description, String defaultValue);

    @MemberOrder(sequence="13")
    UserSetting newLocalDate(
            String user, String name, String description, LocalDate defaultValue);

    @MemberOrder(sequence="14")
    UserSetting newInt(
            String user, String name, String description, Integer defaultValue);

    @MemberOrder(sequence="15")
    UserSetting newLong(
            String user, String name, String description, Long defaultValue);
    
}
