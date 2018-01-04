package org.estatio.module.settings.dom;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

/**
 * Convenience class to implement {@link Setting}.
 */
@DomainObject(editing = Editing.DISABLED)
public abstract class SettingAbstract implements Setting {

    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Title(sequence="10")
    public abstract String getKey();


    @Property(optionality = Optionality.OPTIONAL)
    public abstract String getDescription();

    public abstract SettingType getType();


    @Property(hidden = Where.OBJECT_FORMS)
    public abstract String getValueRaw();


    @Programmatic
    public String valueAsString() {
        ensure(SettingType.STRING);
        return getValueRaw();
    }


    @Programmatic
    public LocalDate valueAsLocalDate() {
        ensure(SettingType.LOCAL_DATE);
        return parseValueAsLocalDate();
    }

    protected LocalDate parseValueAsLocalDate() {
        return LocalDate.parse(getValueRaw(), DATE_FORMATTER);
    }


    @Programmatic
    public Integer valueAsInt() {
        ensure(SettingType.INT);
        return parseValueAsInt();
    }

    protected int parseValueAsInt() {
        return Integer.parseInt(getValueRaw());
    }


    @Programmatic
    public Long valueAsLong() {
        ensure(SettingType.LONG);
        return parseValueAsLong();
    }
    
    protected long parseValueAsLong() {
        return Long.parseLong(getValueRaw());
    }
    

    @Programmatic
    public Boolean valueAsBoolean() {
        ensure(SettingType.BOOLEAN);
        return parseValueAsBoolean();
    }

    protected boolean parseValueAsBoolean() {
        return Boolean.parseBoolean(getValueRaw());
    }


    @Property(hidden = Where.ALL_TABLES)
    public String getValueAsString() {
        return hideValueAsString() ? null : getValueRaw();
    }
    public boolean hideValueAsString() {
        return typeIsNot(SettingType.STRING);
    }

    @Property(hidden = Where.ALL_TABLES)
    public LocalDate getValueAsLocalDate() {
        return hideValueAsLocalDate() ? null : parseValueAsLocalDate();
    }
    public boolean hideValueAsLocalDate() {
        return typeIsNot(SettingType.LOCAL_DATE);
    }
    
    @Property(hidden = Where.ALL_TABLES)
    public Integer getValueAsInt() {
        return hideValueAsInt() ? null : parseValueAsInt();
    }
    public boolean hideValueAsInt() {
        return typeIsNot(SettingType.INT);
    }
    
    @Property(hidden = Where.ALL_TABLES)
    public Long getValueAsLong() {
        return hideValueAsLong() ? null : parseValueAsLong();
    }
    public boolean hideValueAsLong() {
        return typeIsNot(SettingType.LONG);
    }

    @Property(hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Value")
    public Boolean getValueAsBoolean() {
        return hideValueAsBoolean() ? null : parseValueAsBoolean();
    }
    public boolean hideValueAsBoolean() {
        return typeIsNot(SettingType.BOOLEAN);
    }


    private void ensure(SettingType settingType) {
        if(typeIsNot(settingType)) {
            throw new IllegalStateException("Setting '" + getKey() + "' is of type " + getType() + ", not of type " + settingType);
        }
    }
    
    protected boolean typeIsNot(SettingType settingType) {
        return getType() != settingType;
    }
    
}
