package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;



public class ApplicationSettingKeyCreatorTestEach_create extends ApplicationSettingCreatorTestAbstract_create {

    static enum DummyApplicationSettingKey implements ApplicationSettingCreator {
        someDate(LocalDate.class, "Some date", new LocalDate(2013,4,1)),
        someInt(Integer.class, "Some integer", Integer.MAX_VALUE),
        someLong(Long.class, "Some long", Long.MAX_VALUE),
        someString(String.class, "Some string", "ABC"),
        someBoolean(Boolean.class, "Some string", Boolean.TRUE)
        ;
        
        private final Object defaultValue;
        private final String description;
        private final Class<?> dataType;
        
        private DummyApplicationSettingKey(Class<?> dataType, String description, Object defaultValue) {
            this.dataType = dataType;
            this.description = description;
            this.defaultValue = defaultValue;
        }
        public void create(ApplicationSettingsServiceRW appSettings) {
            Helper.create(this, appSettings);
        }
        public Class<?> getDataType() {
            return dataType;
        }
        public String getDescription() {
            return description;
        }
        public Object getDefaultValue() {
            return defaultValue;
        }
    }
    
    public ApplicationSettingKeyCreatorTestEach_create() {
        super(DummyApplicationSettingKey.values());
    }
    
    

}
